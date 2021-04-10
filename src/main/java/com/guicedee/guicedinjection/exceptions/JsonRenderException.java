package com.guicedee.guicedinjection.exceptions;

import java.io.Serial;

public class JsonRenderException extends RuntimeException
{
	@Serial
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new Module exception.
	 */
	public JsonRenderException()
	{
		//No config required
	}
	
	/**
	 * Instantiates a new Module exception.
	 *
	 * @param message the message
	 */
	public JsonRenderException(String message)
	{
		super(message);
	}
	
	/**
	 * Instantiates a new Module exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public JsonRenderException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Instantiates a new Module exception.
	 *
	 * @param cause the cause
	 */
	public JsonRenderException(Throwable cause)
	{
		super(cause);
	}
	
	/**
	 * Instantiates a new Module exception.
	 *
	 * @param message            the message
	 * @param cause              the cause
	 * @param enableSuppression  the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public JsonRenderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
