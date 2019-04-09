package com.jwebmp.guicedinjection.interfaces;


import com.google.inject.Singleton;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.logger.LogFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages All Concurrent Threaded Jobs that execute asynchronously outside of the EE Context
 */

@Singleton
public class JobService
{
	private static final Logger log = LogFactory.getLog("JobService");
	private static final Map<String, ExecutorService> serviceMap = new ConcurrentHashMap<>();
	private static final Map<String, ScheduledExecutorService> pollingMap = new ConcurrentHashMap<>();

	JobService()
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
	public void removeJob(String pool)
	{
		ExecutorService es = serviceMap.get(pool);
		if (es == null)
		{
			log.warning("Pool " + pool + " was not registered");
			return;
		}
		es.shutdown();
		try
		{
			log.finer("Waiting for pool " + pool + " to shutdown cleanly.");
			es.awaitTermination(1, TimeUnit.MILLISECONDS);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Couldn't shut down pool" + pool + " cleanly in 60 seconds. Forcing.");
		}
		serviceMap.remove(pool);
	}

	/**
	 * Completes and Removes all jobs running from the given pool
	 *
	 * @param pool
	 * 		The pool name to remove
	 */
	public void removePollingJob(String pool)
	{
		ScheduledExecutorService es = pollingMap.get(pool);
		if (es == null)
		{
			log.warning("Repeating Pool " + pool + " was not registered");
			return;
		}
		es.shutdown();
		try
		{
			log.finer("Waiting for repeating  pool " + pool + " to shutdown cleanly.");
			es.awaitTermination(60, TimeUnit.SECONDS);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Couldn't shut down pool" + pool + " cleanly in 60 seconds. Forcing.");
			es.shutdownNow();
		}
		pollingMap.remove(pool);
	}

	/**
	 * Registers a new job pool with a specific service
	 *
	 * @param name
	 * @param executorService
	 */
	public void registerJobPool(String name, ExecutorService executorService)
	{
		if (serviceMap.containsKey(name))
		{
			serviceMap.get(name)
			          .shutdown();
			try
			{
				serviceMap.get(name)
				          .awaitTermination(60, TimeUnit.SECONDS);
			}
			catch (InterruptedException e)
			{
				log.log(Level.WARNING, "Unable to shut down existing job pool specified [" + name + "]", e);
			}
		}
		serviceMap.put(name, executorService);
	}

	/**
	 * Registers a repeating task to be registered and monitored
	 *
	 * @param name
	 * 		The name of the pool
	 * @param executorService
	 * 		The service executor
	 */
	public void registerJobPollingPool(String name, ScheduledExecutorService executorService)
	{
		if (pollingMap.containsKey(name))
		{
			pollingMap.get(name)
			          .shutdown();
			try
			{
				log.finer("Waiting for current job processes to finish...");
				pollingMap.get(name)
				          .awaitTermination(60, TimeUnit.SECONDS);
			}
			catch (Exception e)
			{
				log.log(Level.WARNING, "Unable to shut down existing job pool specified [" + name + "]", e);
			}
		}
		pollingMap.put(name, executorService);
	}

	/**
	 * Adds a static run once job to the monitored collections
	 *
	 * @param jobPoolName
	 * @param thread
	 */
	public ForkJoinPool addJob(String jobPoolName, Runnable thread)
	{
		if (!serviceMap.containsKey(jobPoolName))
		{
			registerJobPool(jobPoolName, Executors.newFixedThreadPool(Runtime.getRuntime()
			                                                                 .availableProcessors()));
		}

		ForkJoinPool service = (ForkJoinPool) serviceMap.get(jobPoolName);
		if (service.getQueuedTaskCount() >= 20)
		{
			log.log(Level.WARNING, "20 Hit, Finishing before next run");
			removeJob(jobPoolName);
			registerJobPool(jobPoolName, Executors.newFixedThreadPool(Runtime.getRuntime()
			                                                                 .availableProcessors()));
		}
		service.submit(thread);
		return service;
	}

	public void waitForJob(String jobName)
	{
		waitForJob(jobName, 20, TimeUnit.SECONDS);
	}

	public void waitForJob(String jobName, long timeout, TimeUnit unit)
	{
		if (!serviceMap.containsKey(jobName))
		{
			return;
		}
		ForkJoinPool service = (ForkJoinPool) serviceMap.get(jobName);
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
			                   value.shutdown();
			                   try
			                   {
				                   value.awaitTermination(60, TimeUnit.SECONDS);
			                   }
			                   catch (Exception e)
			                   {
				                   log.config("Shutting Down Failed. [" + key + "] Forcing");
				                   value.shutdownNow();
			                   }
		                   });
		pollingMap.forEach((key, value) ->
		                   {
			                   log.config("Shutting Down Poll Job [" + key + "]");
			                   value.shutdown();
			                   try
			                   {
				                   value.awaitTermination(60, TimeUnit.SECONDS);
			                   }
			                   catch (Exception e)
			                   {
				                   log.config("Shutting Down Failed. [" + key + "] Forcing");
				                   value.shutdownNow();
			                   }
		                   });
		log.config("All jobs destroyed");
	}
}
