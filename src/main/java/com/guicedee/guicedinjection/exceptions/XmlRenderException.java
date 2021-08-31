package com.guicedee.guicedinjection.exceptions;

public class XmlRenderException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new Module exception.
	 */
	public XmlRenderException()
	{
		//No config required
	}
	
	/**
	 * Instantiates a new Module exception.
	 *
	 * @param message the message
	 */
	public XmlRenderException(String message)
	{
		super(message);
	}
	
	/**
	 * Instantiates a new Module exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public XmlRenderException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Instantiates a new Module exception.
	 *
	 * @param cause the cause
	 */
	public XmlRenderException(Throwable cause)
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
	public XmlRenderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
