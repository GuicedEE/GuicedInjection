package com.jwebmp.guicedinjection.interfaces;


import com.google.inject.Singleton;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.logger.LogFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jwebmp.guicedinjection.GuiceContext.*;

/**
 * Manages All Concurrent Threaded Jobs that execute asynchronously outside of the EE Context
 */

@Singleton
public class JobService implements IGuicePreDestroy<JobService>
{
	private static final Logger log = LogFactory.getLog("JobService");
	private static final Map<String, ExecutorService> serviceMap = new ConcurrentHashMap<>();
	private static final Map<String, ScheduledExecutorService> pollingMap = new ConcurrentHashMap<>();
	public static Integer maxQueueCount = 20;

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
	 * @param pool
	 * 		The pool to remove
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
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Couldn't shut down pool" + pool + " cleanly in 60 seconds. Forcing.");
		}
		serviceMap.remove(pool);
		return es;
	}

	/**
	 * Completes and Removes all jobs running from the given pool
	 *
	 * @param pool
	 * 		The pool name to remove
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
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Couldn't shut down pool" + pool + " cleanly in 60 seconds. Forcing.");
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
		return executorService;
	}

	/**
	 * Registers a repeating task to be registered and monitored
	 *
	 * @param name
	 * 		The name of the pool
	 * @param executorService
	 * 		The service executor
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
		if (!serviceMap.containsKey(jobPoolName))
		{
			registerJobPool(jobPoolName, Executors.newFixedThreadPool(Runtime.getRuntime()
			                                                                 .availableProcessors()));
		}

		ExecutorService service = serviceMap.get(jobPoolName);
		if (getCurrentTaskCount(service) >= maxQueueCount)
		{
			log.log(Level.WARNING, maxQueueCount + " Hit - Finishing before next run");
			removeJob(jobPoolName);
			service = registerJobPool(jobPoolName, Executors.newFixedThreadPool(Runtime.getRuntime()
			                                                                 .availableProcessors()));
		}
		service.execute(thread);
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
		}
		catch (InterruptedException e)
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
		if (!pollingMap.containsKey(jobPoolName))
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
		if (!pollingMap.containsKey(jobPoolName))
		{
			registerJobPollingPool(jobPoolName, Executors.newScheduledThreadPool(Runtime.getRuntime()
			                                                                            .availableProcessors()));
		}
		ScheduledExecutorService service = pollingMap.get(jobPoolName);
		service.scheduleAtFixedRate(thread, initialDelay, delay, unit);
		return service;
	}

	/**
	 * Static instance giver
	 *
	 * @return The running instance
	 */
	public static JobService getInstance()
	{
		return GuiceContext.get(JobService.class);
	}

	/**
	 * Shutdowns
	 */
	public void destroy()
	{
		log.config("Destroying all runnings jobs...");
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
		if(service instanceof ForkJoinPool )
		{
			ForkJoinPool pool = (ForkJoinPool) service;
			return (int) pool.getQueuedTaskCount();
		}else if(service instanceof ThreadPoolExecutor )
		{
			ThreadPoolExecutor executor = (ThreadPoolExecutor) service;
			return (int) executor.getTaskCount();
		}
		return 0;
	}

	@Override
	public void onDestroy()
	{
		destroy();
	}
}
