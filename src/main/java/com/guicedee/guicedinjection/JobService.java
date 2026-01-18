package com.guicedee.guicedinjection;

import com.google.inject.Singleton;
import com.guicedee.client.services.lifecycle.IGuicePreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;


/**
 * Manages concurrent job pools that execute outside of the EE context.
 * <p>
 * Pools can be registered, used for one-off tasks, or scheduled for polling tasks,
 * and will be cleaned up on shutdown via {@link IGuicePreDestroy}.
 */

@Singleton
@Log4j2
public class JobService implements IGuicePreDestroy<JobService>
{
	private final Map<String, ExecutorService> serviceMap = new ConcurrentHashMap<>();
	private final Map<String, ScheduledExecutorService> pollingMap = new ConcurrentHashMap<>();
	private final Map<String, Integer> maxQueueCount = new ConcurrentHashMap<>();

	private final ExecutorServiceSupplier executorServiceSupplier = new ExecutorServiceSupplier();

	@Getter
	@Setter
	private static long defaultWaitTime = 120;
	@Getter
	@Setter
	private static TimeUnit defaultWaitUnit = TimeUnit.SECONDS;

	public static final JobService INSTANCE = new JobService();

	private static final ThreadFactory factory = Thread.ofVirtual().factory();

	static
	{
		INSTANCE.jobCleanup();
	}

	public JobService()
	{
		//No config required
	}

	/**
	 * Returns the names of registered job pools.
	 *
	 * @return the registered job pool names
	 */
	
	public Set<String> getJobPools()
	{
		return serviceMap.keySet();
	}

	/**
	 * Returns the names of registered polling pools.
	 *
	 * @return the registered polling pool names
	 */
	
	public Set<String> getPollingPools()
	{
		return pollingMap.keySet();
	}

	/**
	 * Completes and removes all jobs running in the given pool, waiting for completion.
	 *
	 * @param pool The pool name to remove
	 * @return the removed executor, or {@code null} if not registered
	 */
	
	public ExecutorService removeJob(String pool)
	{
		ExecutorService es = serviceMap.get(pool);
		if (es == null)
		{
			log.warn("Pool " + pool + " was not registered");
			return null;
		}
		waitForJob(pool);
		serviceMap.remove(pool);
		return es;
	}

	/**
	 * Removes all jobs running in the given pool without waiting for completion.
	 *
	 * @param pool The pool name to remove
	 * @return the removed executor, or {@code null} if not registered
	 */
	public ExecutorService removeJobNoWait(String pool)
	{
		ExecutorService es = serviceMap.get(pool);
		if (es == null)
		{
			log.warn("Pool " + pool + " was not registered");
			return null;
		}
		waitForJob(pool,1L,TimeUnit.MILLISECONDS);
		serviceMap.remove(pool);
		return es;
	}

	/**
	 * Completes and removes all scheduled jobs running in the given pool.
	 *
	 * @param pool The pool name to remove
	 * @return the removed executor, or {@code null} if not registered
	 */
	
	public ScheduledExecutorService removePollingJob(String pool)
	{
		ScheduledExecutorService es = pollingMap.get(pool);
		if (es == null)
		{
			log.warn("Repeating Pool " + pool + " was not registered");
			return null;
		}
		waitForJob(pool);
		pollingMap.remove(pool);
		return es;
	}

	/**
	 * Registers a new job pool, replacing any existing pool of the same name.
	 *
	 * @param name the pool name
	 * @param executorService the executor service to register
	 * @return the registered executor service
	 */
	
	public ExecutorService registerJobPool(String name, ExecutorService executorService)
	{
		if (serviceMap.containsKey(name))
		{
			removeJob(name);
		}
		serviceMap.put(name, executorService);
		if (!maxQueueCount.containsKey(name))
		{
			maxQueueCount.put(name, 20);
		}
		if (executorService instanceof ForkJoinPool)
		{
			ForkJoinPool pool = (ForkJoinPool) executorService;
		}
		else if (executorService instanceof ThreadPoolExecutor)
		{
			ThreadPoolExecutor executor = (ThreadPoolExecutor) executorService;
			executor.setMaximumPoolSize(maxQueueCount.get(name));
			executor.setKeepAliveTime(defaultWaitTime, defaultWaitUnit);
			executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
		}

		return executorService;
	}

	/**
	 * Registers a polling pool for scheduled tasks, replacing any existing pool of the same name.
	 *
	 * @param name the pool name
	 * @param executorService the scheduled executor service to register
	 * @return the registered scheduled executor service
	 */
	
	public ScheduledExecutorService registerJobPollingPool(String name, ScheduledExecutorService executorService)
	{
		if (pollingMap.containsKey(name))
		{
			removeJob(name);
		}
		pollingMap.put(name, executorService);
		return executorService;
	}

	/**
	 * Adds a one-off runnable job to the named pool, creating the pool if needed.
	 *
	 * @param jobPoolName the pool name
	 * @param thread the task to execute
	 * @return the executor service used for execution
	 */
	
	public ExecutorService addJob(String jobPoolName, Runnable thread)
	{
		if (!serviceMap.containsKey(jobPoolName) || serviceMap
				                                            .get(jobPoolName)
				                                            .isTerminated() || serviceMap
						                                                               .get(jobPoolName)
						                                                               .isShutdown())
		{
			registerJobPool(jobPoolName, executorServiceSupplier.get());
		}

		ExecutorService service = serviceMap.get(jobPoolName);
		if (getCurrentTaskCount(service) >= maxQueueCount.get(jobPoolName))
		{
			log.debug(maxQueueCount + " Hit - Finishing before next run");
			removeJob(jobPoolName);
			service = registerJobPool(jobPoolName, executorServiceSupplier.get());
		}
		service.execute(thread);
		return service;
	}

	/**
	 * Adds a one-off callable task to the named pool, creating the pool if needed.
	 *
	 * @param jobPoolName the pool name
	 * @param thread the task to execute
	 * @return a future representing task completion
	 */
	
	public Future<?> addTask(String jobPoolName, Callable<?> thread)
	{
		if (!serviceMap.containsKey(jobPoolName) || serviceMap
				                                            .get(jobPoolName)
				                                            .isTerminated() || serviceMap
						                                                               .get(jobPoolName)
						                                                               .isShutdown())
		{
			registerJobPool(jobPoolName, executorServiceSupplier.get());
		}

		ExecutorService service = serviceMap.get(jobPoolName);
		if (getCurrentTaskCount(service) >= maxQueueCount.get(jobPoolName))
		{
			log.debug(maxQueueCount + " Hit - Finishing before next run");
			removeJob(jobPoolName);
			service = registerJobPool(jobPoolName, executorServiceSupplier.get());
		}
		return service.submit(thread);
	}

	
	/**
	 * Shuts down the named pool and waits for completion using default timeout settings.
	 *
	 * @param jobName the pool name
	 */
	public void waitForJob(String jobName)
	{
		waitForJob(jobName, defaultWaitTime, defaultWaitUnit);
	}

	
	/**
	 * Shuts down the named pool and waits for completion.
	 *
	 * @param jobName the pool name
	 * @param timeout the maximum time to wait
	 * @param unit the time unit for the timeout
	 */
	public void waitForJob(String jobName, long timeout, TimeUnit unit)
	{
		if (!serviceMap.containsKey(jobName))
		{
			return;
		}
		ExecutorService service = serviceMap.get(jobName);
		service.shutdown();
		try
		{
			service.awaitTermination(timeout, unit);
		}
		catch (InterruptedException e)
		{
			log.warn("Thread didn't close cleanly, make sure running times are acceptable", e);
			service.shutdownNow();
		}
		if (!service.isTerminated())
		{
			service.shutdownNow();
		}
		service.close();
	}

	private ExecutorService jobCleanup()
	{
		ScheduledExecutorService jobsShutdownNotClosed = addPollingJob("JobsShutdownNotClosed", () -> {
			for (String jobPool : getJobPools())
			{
				ExecutorService executorService = serviceMap.get(jobPool);
				if (executorService.isShutdown() && !executorService.isTerminated())
				{
					log.debug("Closing unfinished job - " + jobPool);
					removeJob(jobPool);
				}
				if (executorService.isShutdown() && executorService.isTerminated())
				{
					log.debug("Cleaning terminated job - " + jobPool);
					executorService.close();
					serviceMap.remove(jobPool);
				}
			}
		}, 2, TimeUnit.MINUTES);

		return jobsShutdownNotClosed;
	}

	/**
	 * Registers a polling job that runs at a fixed rate after an initial delay of 1 unit.
	 *
	 * @param jobPoolName the pool name
	 * @param thread the task to execute
	 * @param delay the fixed delay between executions
	 * @param unit the time unit for the delay
	 * @return the scheduled executor service used for execution
	 */
	
	public ScheduledExecutorService addPollingJob(String jobPoolName, Runnable thread, long delay, TimeUnit unit)
	{
		if (!pollingMap.containsKey(jobPoolName) || pollingMap
				                                            .get(jobPoolName)
				                                            .isTerminated() || pollingMap
						                                                               .get(jobPoolName)
						                                                               .isShutdown())
		{
			registerJobPollingPool(jobPoolName,
			                       Executors.newSingleThreadScheduledExecutor(factory));
		}
		ScheduledExecutorService service = pollingMap.get(jobPoolName);
		service.scheduleAtFixedRate(thread, 1L, delay, unit);
		return service;
	}

	/**
	 * Registers a polling job that runs at a fixed rate with an explicit initial delay.
	 *
	 * @param jobPoolName the pool name
	 * @param thread the task to execute
	 * @param initialDelay the initial delay before first execution
	 * @param delay the fixed delay between executions
	 * @param unit the time unit for the delays
	 * @return the scheduled executor service used for execution
	 */
	
	public ScheduledExecutorService addPollingJob(String jobPoolName, Runnable thread, long initialDelay, long delay, TimeUnit unit)
	{
		ScheduledExecutorService scheduledExecutorService = null;
		if (!pollingMap.containsKey(jobPoolName) || pollingMap
				                                            .get(jobPoolName)
				                                            .isTerminated() || pollingMap
						                                                               .get(jobPoolName)
						                                                               .isShutdown())
		{
			scheduledExecutorService = registerJobPollingPool(jobPoolName,
																					   Executors.newSingleThreadScheduledExecutor());
		}
		scheduledExecutorService.scheduleAtFixedRate(thread, initialDelay, delay, unit);
		return scheduledExecutorService;
	}

	/**
	 * Shuts down all job and polling pools and waits for completion.
	 */
	
	public void destroy()
	{
		log.info("Destroying all running jobs...");
		serviceMap.forEach((key, value) -> {
			log.info("Shutting Down [" + key + "]");
			removeJob(key);
		});
		pollingMap.forEach((key, value) -> {
			log.info("Shutting Down Poll Job [" + key + "]");
			removePollingJob(key);
		});
		log.info("All jobs destroyed");
	}

	private int getCurrentTaskCount(ExecutorService service)
	{
		if (service instanceof ForkJoinPool)
		{
			ForkJoinPool pool = (ForkJoinPool) service;
			return (int) pool.getQueuedTaskCount();
		}
		else if (service instanceof ThreadPoolExecutor)
		{
			ThreadPoolExecutor executor = (ThreadPoolExecutor) service;
			return (int) executor.getTaskCount();
		}
		return 0;
	}

	/**
	 * Sets the maximum queue count for a named pool.
	 *
	 * @param queueName the pool name
	 * @param queueCount the maximum queued task count
	 */
	public void setMaxQueueCount(String queueName, int queueCount)
	{
		maxQueueCount.put(queueName, queueCount);
	}

	
	/**
	 * Lifecycle hook called during application shutdown.
	 */
	public void onDestroy()
	{
		destroy();
	}

	
	/**
	 * Returns the sort order for pre-destroy services.
	 *
	 * @return the sort order value
	 */
	public Integer sortOrder()
	{
		return Integer.MIN_VALUE + 8;
	}


}
