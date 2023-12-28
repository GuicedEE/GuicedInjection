package com.guicedee.guicedinjection;

import com.google.inject.Singleton;
import com.guicedee.guicedinjection.interfaces.IGuicePreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;


/**
 * Manages All Concurrent Threaded Jobs that execute asynchronously outside of the EE Context
 */

@Singleton
@Log
public class JobService
				implements IGuicePreDestroy<JobService>
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
	
	private static final JobService INSTANCE = new JobService();
	
	public static JobService getInstance(){
		return INSTANCE;
	}
	
	public JobService()
	{
		//No config required
	}
	
	/**
	 * Gets a list of all job pools currently registered
	 *
	 * @return
	 */
	public Set<String> getJobPools()
	{
		return serviceMap.keySet();
	}
	
	/**
	 * Returns the list of repeating task pools registered
	 *
	 * @return
	 */
	public Set<String> getPollingPools()
	{
		return pollingMap.keySet();
	}
	
	/**
	 * Completes and Removes all jobs running from the given pool
	 *
	 * @param pool The pool to remove
	 */
	public ExecutorService removeJob(String pool)
	{
		ExecutorService es = serviceMap.get(pool);
		if (es == null)
		{
			log.warning("Pool " + pool + " was not registered");
			return null;
		}
		
		es.shutdown();
		try
		{
			log.finer("Waiting for pool " + pool + " to shutdown cleanly.");
			es.awaitTermination(defaultWaitTime, defaultWaitUnit);
		} catch (Exception e)
		{
			log.log(Level.SEVERE, "Couldn't shut down pool" + pool + " cleanly in 60 seconds. Forcing.");
		}
		if (!es.isShutdown())
		{
			es.shutdownNow();
		}
		es.close();
		serviceMap.remove(pool);
		return es;
	}
	
	/**
	 * Completes and Removes all jobs running from the given pool
	 *
	 * @param pool The pool name to remove
	 */
	public ScheduledExecutorService removePollingJob(String pool)
	{
		ScheduledExecutorService es = pollingMap.get(pool);
		if (es == null)
		{
			log.warning("Repeating Pool " + pool + " was not registered");
			return null;
		}
		es.shutdown();
		try
		{
			log.finer("Waiting for repeating  pool " + pool + " to shutdown cleanly.");
			es.awaitTermination(defaultWaitTime, defaultWaitUnit);
		} catch (Exception e)
		{
			log.log(Level.SEVERE, "Couldn't shut down pool" + pool + " cleanly in 60 seconds. Forcing.");
			es.shutdownNow();
		}
		if (!es.isTerminated())
		{
			es.shutdownNow();
		}
		pollingMap.remove(pool);
		return es;
	}
	
	/**
	 * Registers a new job pool with a specific service
	 *
	 * @param name
	 * @param executorService
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
		} else if (executorService instanceof ThreadPoolExecutor)
		{
			ThreadPoolExecutor executor = (ThreadPoolExecutor) executorService;
			executor.setMaximumPoolSize(maxQueueCount.get(name));
			executor.setKeepAliveTime(defaultWaitTime, defaultWaitUnit);
			executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
		}
		
		return executorService;
	}
	
	/**
	 * Registers a repeating task to be registered and monitored
	 *
	 * @param name            The name of the pool
	 * @param executorService The service executor
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
	 * Adds a static run once job to the monitored collections
	 *
	 * @param jobPoolName
	 * @param thread
	 */
	public ExecutorService addJob(String jobPoolName, Runnable thread)
	{
		if (!serviceMap.containsKey(jobPoolName) || serviceMap.get(jobPoolName).isTerminated() || serviceMap.get(jobPoolName).isShutdown())
		{
			registerJobPool(jobPoolName, executorServiceSupplier.get());
		}
		
		ExecutorService service = serviceMap.get(jobPoolName);
		if (getCurrentTaskCount(service) >= maxQueueCount.get(jobPoolName))
		{
			log.log(Level.FINER, maxQueueCount + " Hit - Finishing before next run");
			removeJob(jobPoolName);
			service = registerJobPool(jobPoolName,executorServiceSupplier.get());
		}
		service.execute(thread);
		return service;
	}
	
	/**
	 * Adds a static run once job to the monitored collections
	 *
	 * @param jobPoolName
	 * @param thread
	 */
	public ExecutorService addJob(String jobPoolName, Callable<?> thread)
	{
		if (!serviceMap.containsKey(jobPoolName) || serviceMap.get(jobPoolName).isTerminated() || serviceMap.get(jobPoolName).isShutdown())
		{
			registerJobPool(jobPoolName, executorServiceSupplier.get());
		}
		
		ExecutorService service = serviceMap.get(jobPoolName);
		if (getCurrentTaskCount(service) >= maxQueueCount.get(jobPoolName))
		{
			log.log(Level.FINER, maxQueueCount + " Hit - Finishing before next run");
			removeJob(jobPoolName);
			service = registerJobPool(jobPoolName, executorServiceSupplier.get());
		}
		service.submit(thread);
		return service;
	}
	
	public void waitForJob(String jobName)
	{
		waitForJob(jobName, defaultWaitTime, defaultWaitUnit);
	}
	
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
		} catch (InterruptedException e)
		{
			log.log(Level.WARNING, "Thread didn't close cleanly, make sure running times are acceptable", e);
		}
	}
	
	/**
	 * Adds a static run once job to the monitored collections
	 *
	 * @param jobPoolName
	 * @param thread
	 */
	public ScheduledExecutorService addPollingJob(String jobPoolName, Runnable thread, long delay, TimeUnit unit)
	{
		if (!pollingMap.containsKey(jobPoolName) || pollingMap.get(jobPoolName).isTerminated() || pollingMap.get(jobPoolName).isShutdown())
		{
			registerJobPollingPool(jobPoolName, Executors.newScheduledThreadPool(Runtime.getRuntime()
							.availableProcessors()));
		}
		ScheduledExecutorService service = pollingMap.get(jobPoolName);
		service.scheduleAtFixedRate(thread, 1L, delay, unit);
		return service;
	}
	
	/**
	 * Adds a static run once job to the monitored collections
	 *
	 * @param jobPoolName
	 * @param thread
	 */
	public ScheduledExecutorService addPollingJob(String jobPoolName, Runnable thread, long initialDelay, long delay, TimeUnit unit)
	{
		if (!pollingMap.containsKey(jobPoolName) || pollingMap.get(jobPoolName).isTerminated() || pollingMap.get(jobPoolName).isShutdown())
		{
			registerJobPollingPool(jobPoolName, Executors.newScheduledThreadPool(Runtime.getRuntime()
							.availableProcessors()));
		}
		ScheduledExecutorService service = pollingMap.get(jobPoolName);
		service.scheduleAtFixedRate(thread, initialDelay, delay, unit);
		return service;
	}
	
	/**
	 * Shutdowns
	 */
	public void destroy()
	{
		log.config("Destroying all running jobs...");
		serviceMap.forEach((key, value) ->
		{
			log.config("Shutting Down [" + key + "]");
			removeJob(key);
		});
		pollingMap.forEach((key, value) ->
		{
			log.config("Shutting Down Poll Job [" + key + "]");
			removePollingJob(key);
		});
		log.config("All jobs destroyed");
	}
	
	private int getCurrentTaskCount(ExecutorService service)
	{
		if (service instanceof ForkJoinPool)
		{
			ForkJoinPool pool = (ForkJoinPool) service;
			return (int) pool.getQueuedTaskCount();
		} else if (service instanceof ThreadPoolExecutor)
		{
			ThreadPoolExecutor executor = (ThreadPoolExecutor) service;
			return (int) executor.getTaskCount();
		}
		return 0;
	}
	
	public void setMaxQueueCount(String queueName, int queueCount)
	{
		maxQueueCount.put(queueName, queueCount);
	}
	
	@Override
	public void onDestroy()
	{
		destroy();
	}
	
	@Override
	public Integer sortOrder()
	{
		return Integer.MIN_VALUE + 8;
	}
	
	
}
