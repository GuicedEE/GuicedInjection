package com.guicedee.guicedinjection;

import com.google.inject.Singleton;
import com.guicedee.client.services.lifecycle.IGuicePreDestroy;
import com.guicedee.vertx.spi.VertXPreStartup;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Manages concurrent job pools running on the shared Vert.x instance.
 * <p>
 * <b>One-off tasks</b> execute via {@code Vertx.executeBlocking()} on the Vert.x worker pool.
 * <b>Periodic tasks</b> use {@code Vertx.setPeriodic()} with worker-thread execution.
 * <b>Cron-scheduled tasks</b> use chained {@code Vertx.setTimer()} calls with a built-in
 * {@link CronExpression} parser for next-fire-time calculation.
 * <p>
 * All pools are cleaned up on shutdown via {@link IGuicePreDestroy}.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * JobService jobs = JobService.INSTANCE;
 *
 * // One-off
 * jobs.registerJob("import", 100);
 * jobs.addJob("import", () -> processFile(file));
 *
 * // Periodic polling
 * jobs.addPollingJob("heartbeat", () -> ping(), 0, 30, TimeUnit.SECONDS);
 *
 * // Cron
 * jobs.addCronJob("nightly-report", "0 2 * * *", () -> generateReport());
 *
 * // Cleanup
 * jobs.removeJob("import");
 * }</pre>
 */
@Singleton
@Log4j2
public class JobService implements IGuicePreDestroy<JobService>
{
	/**
	 * Tracks periodic/cron timer IDs per pool name for cancellation.
	 */
	private final Map<String, List<Long>> timerIds = new ConcurrentHashMap<>();

	/**
	 * Maximum queued task count per named pool (advisory, used for executeBlocking overflow guard).
	 */
	private final Map<String, Integer> maxQueueCount = new ConcurrentHashMap<>();

	/**
	 * Tracks active executeBlocking task count per pool for overflow guard.
	 */
	private final Map<String, AtomicInteger> activeTaskCount = new ConcurrentHashMap<>();

	/**
	 * Tracks parsed cron expressions for introspection/rescheduling.
	 */
	private final Map<String, CronExpression> cronJobs = new ConcurrentHashMap<>();

	@Getter
	@Setter
	private static long defaultWaitTime = 120;

	@Getter
	@Setter
	private static TimeUnit defaultWaitUnit = TimeUnit.SECONDS;

	/**
	 * Singleton instance for static access outside of Guice injection.
	 */
	public static final JobService INSTANCE = new JobService();

	/**
	 * Default Constructor for JobService
	 */
	public JobService()
	{
		//No config required
	}

	/**
	 * Returns the Vert.x instance from the GuicedEE lifecycle.
	 */
	private Vertx vertx()
	{
		return VertXPreStartup.getVertx();
	}

	// ────────────────────────────────────────────────────────────────────────
	// Pool Registration
	// ────────────────────────────────────────────────────────────────────────

	/**
	 * Pre-registers a named job pool with the given maximum concurrent task count.
	 * If the pool already exists it is replaced after draining.
	 *
	 * @param name          the pool name
	 * @param maxQueueCount the maximum number of concurrent tasks before overflow warnings
	 */
	public void registerJob(String name, int maxQueueCount)
	{
		if (timerIds.containsKey(name))
		{
			removeJob(name);
		}
		ensurePool(name);
		this.maxQueueCount.put(name, maxQueueCount);
		log.debug("Registered job pool [{}] with max queue {}", name, maxQueueCount);
	}

	/**
	 * Pre-registers a named job pool with the default maximum queue count (20).
	 *
	 * @param name the pool name
	 */
	public void registerJob(String name)
	{
		registerJob(name, 20);
	}

	/**
	 * Convenience method that registers a polling job pool and immediately schedules a task.
	 *
	 * @param name         the pool name
	 * @param task         the task to execute
	 * @param initialDelay the initial delay before first execution
	 * @param delay        the fixed delay between executions
	 * @param unit         the time unit for delays
	 */
	public void registerPollingJob(String name, Runnable task, long initialDelay, long delay, TimeUnit unit)
	{
		if (timerIds.containsKey(name))
		{
			removeJob(name);
		}
		addPollingJob(name, task, initialDelay, delay, unit);
	}

	// ────────────────────────────────────────────────────────────────────────
	// Pool Queries
	// ────────────────────────────────────────────────────────────────────────

	/**
	 * Returns the names of all registered job pools.
	 *
	 * @return an unmodifiable view of pool names
	 */
	public Set<String> getJobPools()
	{
		return Collections.unmodifiableSet(timerIds.keySet());
	}

	/**
	 * Returns the names of all registered polling pools.
	 *
	 * @return an unmodifiable view of polling pool names
	 */
	public Set<String> getPollingPools()
	{
		return Collections.unmodifiableSet(timerIds.keySet());
	}

	/**
	 * Returns the names of all registered cron job pools.
	 *
	 * @return an unmodifiable view of cron pool names
	 */
	public Set<String> getCronPools()
	{
		return Collections.unmodifiableSet(cronJobs.keySet());
	}

	/**
	 * Returns the current active task count for a named pool.
	 *
	 * @param poolName the pool name
	 * @return the number of currently executing tasks, or 0 if the pool is not registered
	 */
	public int getActiveTaskCount(String poolName)
	{
		AtomicInteger count = activeTaskCount.get(poolName);
		return count != null ? count.get() : 0;
	}

	/**
	 * Returns the configured maximum queue count for a named pool.
	 *
	 * @param poolName the pool name
	 * @return the max queue count, or 20 (default) if not explicitly configured
	 */
	public int getMaxQueueCount(String poolName)
	{
		return maxQueueCount.getOrDefault(poolName, 20);
	}

	/**
	 * Checks whether a named pool is registered.
	 *
	 * @param poolName the pool name
	 * @return true if the pool exists
	 */
	public boolean isRegistered(String poolName)
	{
		return timerIds.containsKey(poolName);
	}

	// ────────────────────────────────────────────────────────────────────────
	// Pool Removal
	// ────────────────────────────────────────────────────────────────────────

	/**
	 * Cancels and removes all timers and tracking for the given pool.
	 *
	 * @param pool the pool name to remove
	 */
	public void removeJob(String pool)
	{
		List<Long> ids = timerIds.remove(pool);
		if (ids == null)
		{
			log.warn("Pool [{}] was not registered", pool);
			return;
		}
		cancelTimers(ids);
		activeTaskCount.remove(pool);
		cronJobs.remove(pool);
		maxQueueCount.remove(pool);
		log.debug("Removed pool [{}]", pool);
	}

	/**
	 * Cancels and removes all timers for the given pool (alias for {@link #removeJob}).
	 *
	 * @param pool the pool name to remove
	 */
	public void removeJobNoWait(String pool)
	{
		removeJob(pool);
	}

	/**
	 * Cancels and removes a polling/cron pool (alias for {@link #removeJob}).
	 *
	 * @param pool the pool name to remove
	 */
	public void removePollingJob(String pool)
	{
		removeJob(pool);
	}

	// ────────────────────────────────────────────────────────────────────────
	// One-off Jobs
	// ────────────────────────────────────────────────────────────────────────

	/**
	 * Submits a one-off runnable to the named pool, executing on the Vert.x worker pool.
	 * Auto-registers the pool if it does not exist.
	 *
	 * @param jobPoolName the pool name
	 * @param task        the task to execute
	 * @return a Vert.x Future that completes when the task finishes
	 */
	public Future<Void> addJob(String jobPoolName, Runnable task)
	{
		ensurePool(jobPoolName);
		warnIfOverflow(jobPoolName);
		AtomicInteger counter = activeTaskCount.get(jobPoolName);
		counter.incrementAndGet();

		return vertx().executeBlocking(() -> {
			try
			{
				task.run();
			}
			finally
			{
				counter.decrementAndGet();
			}
			return null;
		});
	}

	/**
	 * Submits a one-off callable to the named pool, executing on the Vert.x worker pool.
	 * Auto-registers the pool if it does not exist.
	 *
	 * @param jobPoolName the pool name
	 * @param task        the callable to execute
	 * @param <T>         the result type
	 * @return a Vert.x Future representing the task result
	 */
	public <T> Future<T> addTask(String jobPoolName, Callable<T> task)
	{
		ensurePool(jobPoolName);
		warnIfOverflow(jobPoolName);
		AtomicInteger counter = activeTaskCount.get(jobPoolName);
		counter.incrementAndGet();

		return vertx().executeBlocking(() -> {
			try
			{
				return task.call();
			}
			finally
			{
				counter.decrementAndGet();
			}
		});
	}

	/**
	 * Submits a one-off runnable that will execute after the specified delay.
	 *
	 * @param jobPoolName the pool name
	 * @param task        the task to execute
	 * @param delay       the delay before execution
	 * @param unit        the time unit for the delay
	 */
	public void addDelayedJob(String jobPoolName, Runnable task, long delay, TimeUnit unit)
	{
		ensurePool(jobPoolName);
		long delayMs = unit.toMillis(delay);
		long timerId = vertx().setTimer(delayMs, id -> {
			List<Long> ids = timerIds.get(jobPoolName);
			if (ids != null)
			{
				ids.remove(id);
			}
			vertx().executeBlocking(() -> {
				task.run();
				return null;
			});
		});
		timerIds.get(jobPoolName).add(timerId);
	}

	// ────────────────────────────────────────────────────────────────────────
	// Periodic Polling Jobs
	// ────────────────────────────────────────────────────────────────────────

	/**
	 * Registers a periodic job that runs at a fixed rate after an initial delay of 1 unit.
	 *
	 * @param jobPoolName the pool name
	 * @param task        the task to execute
	 * @param delay       the fixed delay between executions
	 * @param unit        the time unit for the delay
	 */
	public void addPollingJob(String jobPoolName, Runnable task, long delay, TimeUnit unit)
	{
		addPollingJob(jobPoolName, task, 1L, delay, unit);
	}

	/**
	 * Registers a periodic job that runs at a fixed rate with an explicit initial delay.
	 *
	 * @param jobPoolName  the pool name
	 * @param task         the task to execute
	 * @param initialDelay the initial delay before first execution
	 * @param delay        the fixed delay between executions
	 * @param unit         the time unit for the delays
	 */
	public void addPollingJob(String jobPoolName, Runnable task, long initialDelay, long delay, TimeUnit unit)
	{
		ensurePool(jobPoolName);
		long delayMs = unit.toMillis(delay);
		long initialMs = unit.toMillis(initialDelay);

		long timerId = vertx().setPeriodic(initialMs, delayMs, id ->
				vertx().executeBlocking(() -> {
					task.run();
					return null;
				})
		);

		timerIds.get(jobPoolName).add(timerId);
		log.debug("Registered periodic job [{}] delay={}ms initial={}ms timerId={}",
				jobPoolName, delayMs, initialMs, timerId);
	}

	// ────────────────────────────────────────────────────────────────────────
	// Cron Jobs
	// ────────────────────────────────────────────────────────────────────────

	/**
	 * Registers a cron-scheduled job using a standard 5-field UNIX cron expression.
	 * <p>
	 * The job fires at each matching time using chained Vert.x one-shot timers.
	 * Supported syntax: values, ranges, steps, lists, wildcards, named days/months.
	 * <p>
	 * Examples:
	 * <pre>
	 * "0 * * * *"       - every hour at :00
	 * "0/15 * * * *"    - every 15 minutes
	 * "0 2 * * MON-FRI" - weekdays at 02:00
	 * "30 4 1 * *"      - 1st of month at 04:30
	 * "0 0 * * 0"       - every Sunday at midnight
	 * </pre>
	 *
	 * @param jobPoolName    the pool name (used for cancellation and lookup)
	 * @param cronExpression a 5-field UNIX cron expression
	 * @param task           the task to execute
	 */
	public void addCronJob(String jobPoolName, String cronExpression, Runnable task)
	{
		ensurePool(jobPoolName);
		CronExpression cron = CronExpression.parse(cronExpression);
		cronJobs.put(jobPoolName, cron);
		scheduleCronExecution(jobPoolName, cron, task);
		log.info("Registered cron job [{}] expression='{}'", jobPoolName, cronExpression);
	}

	/**
	 * Returns the parsed cron expression for the given pool, if registered.
	 *
	 * @param jobPoolName the pool name
	 * @return the cron expression, or empty if not a cron pool
	 */
	public Optional<CronExpression> getCronExpression(String jobPoolName)
	{
		return Optional.ofNullable(cronJobs.get(jobPoolName));
	}

	private void scheduleCronExecution(String jobPoolName, CronExpression cron, Runnable task)
	{
		Optional<Duration> nextDuration = cron.timeToNextExecution(ZonedDateTime.now());
		if (nextDuration.isEmpty())
		{
			log.warn("Cron job [{}] has no future execution time - not scheduling", jobPoolName);
			return;
		}
		long delayMs = Math.max(nextDuration.get().toMillis(), 1);
		log.trace("Cron job [{}] next fire in {}ms", jobPoolName, delayMs);

		long timerId = vertx().setTimer(delayMs, id -> {
			// Remove this one-shot timer from tracking
			List<Long> ids = timerIds.get(jobPoolName);
			if (ids != null)
			{
				ids.remove(id);
			}

			// Execute the task on a worker thread, then reschedule
			vertx().executeBlocking(() -> {
				task.run();
				return null;
			}).onComplete(ar -> {
				if (ar.failed())
				{
					log.error("Cron job [{}] execution failed", jobPoolName, ar.cause());
				}
				// Reschedule only if pool is still registered
				if (timerIds.containsKey(jobPoolName))
				{
					scheduleCronExecution(jobPoolName, cron, task);
				}
			});
		});

		List<Long> ids = timerIds.get(jobPoolName);
		if (ids != null)
		{
			ids.add(timerId);
		}
	}

	// ────────────────────────────────────────────────────────────────────────
	// Backward Compatibility
	// ────────────────────────────────────────────────────────────────────────

	/**
	 * No-op kept for backward compatibility.
	 * Vert.x manages its own thread pools; explicit waiting is not needed.
	 *
	 * @param jobName the pool name
	 * @deprecated use {@link Future#toCompletionStage()} on the returned future instead
	 */
	@Deprecated(since = "2.1.0")
	public void waitForJob(String jobName)
	{
		// No-op: Vert.x manages execution
	}

	/**
	 * No-op kept for backward compatibility.
	 * Vert.x manages its own thread pools; explicit waiting is not needed.
	 *
	 * @param jobName the pool name
	 * @param timeout ignored
	 * @param unit    ignored
	 * @deprecated use {@link Future#toCompletionStage()} on the returned future instead
	 */
	@Deprecated(since = "2.1.0")
	public void waitForJob(String jobName, long timeout, TimeUnit unit)
	{
		// No-op: Vert.x manages execution
	}

	/**
	 * No-op kept for backward compatibility.
	 * Pool registration is handled implicitly by {@link #addJob} or explicitly by {@link #registerJob(String, int)}.
	 *
	 * @param name            the pool name
	 * @param executorService ignored
	 * @return null
	 * @deprecated use {@link #registerJob(String, int)} instead
	 */
	@Deprecated(since = "2.1.0")
	public Object registerJobPool(String name, Object executorService)
	{
		ensurePool(name);
		return null;
	}

	/**
	 * No-op kept for backward compatibility.
	 *
	 * @param name            the pool name
	 * @param executorService ignored
	 * @return null
	 * @deprecated use {@link #registerPollingJob} or {@link #addPollingJob} instead
	 */
	@Deprecated(since = "2.1.0")
	public Object registerJobPollingPool(String name, Object executorService)
	{
		ensurePool(name);
		return null;
	}

	// ────────────────────────────────────────────────────────────────────────
	// Configuration
	// ────────────────────────────────────────────────────────────────────────

	/**
	 * Sets the maximum concurrent task count for a named pool.
	 * Tasks beyond this limit will still execute but a debug warning is logged.
	 *
	 * @param queueName  the pool name
	 * @param queueCount the maximum concurrent task count
	 */
	public void setMaxQueueCount(String queueName, int queueCount)
	{
		maxQueueCount.put(queueName, queueCount);
	}

	// ────────────────────────────────────────────────────────────────────────
	// Lifecycle
	// ────────────────────────────────────────────────────────────────────────

	/**
	 * Shuts down all job, polling, and cron pools by cancelling all Vert.x timers.
	 */
	public void destroy()
	{
		log.info("Destroying all running jobs...");
		Set<String> pools = new HashSet<>(timerIds.keySet());
		for (String pool : pools)
		{
			log.info("Shutting Down [{}]", pool);
			List<Long> ids = timerIds.remove(pool);
			if (ids != null)
			{
				cancelTimers(ids);
			}
		}
		activeTaskCount.clear();
		cronJobs.clear();
		maxQueueCount.clear();
		log.info("All jobs destroyed");
	}

	/**
	 * Lifecycle hook called during application shutdown.
	 */
	@Override
	public void onDestroy()
	{
		destroy();
	}

	/**
	 * Returns the sort order for pre-destroy services.
	 * Runs very early to stop background work before other services shut down.
	 *
	 * @return the sort order value
	 */
	@Override
	public Integer sortOrder()
	{
		return Integer.MIN_VALUE + 8;
	}

	// ────────────────────────────────────────────────────────────────────────
	// Internal
	// ────────────────────────────────────────────────────────────────────────

	private void ensurePool(String name)
	{
		timerIds.computeIfAbsent(name, k -> Collections.synchronizedList(new ArrayList<>()));
		maxQueueCount.putIfAbsent(name, 20);
		activeTaskCount.computeIfAbsent(name, k -> new AtomicInteger(0));
	}

	private void warnIfOverflow(String jobPoolName)
	{
		int max = maxQueueCount.getOrDefault(jobPoolName, 20);
		AtomicInteger counter = activeTaskCount.get(jobPoolName);
		if (counter != null && counter.get() >= max)
		{
			log.debug("[{}] active tasks ({}) >= max ({}), new task will queue",
					jobPoolName, counter.get(), max);
		}
	}

	private void cancelTimers(List<Long> ids)
	{
		Vertx v = vertx();
		for (Long id : ids)
		{
			v.cancelTimer(id);
		}
	}
}
