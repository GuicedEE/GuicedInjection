package za.co.mmagon.guiceinjection.exceptions;

/**
 * Generic exception to mark that no connection information was supplied
 */
public class NoConnectionInfoException extends RuntimeException
{
	/**
	 * Generic exception to mark that no connection information was supplied
	 */
	public NoConnectionInfoException()
	{
		//Nothing needed
	}

	/**
	 * Generic exception to mark that no connection information was supplied
	 *
	 * @param message
	 */
	public NoConnectionInfoException(String message)
	{
		super(message);
	}

	/**
	 * Generic exception to mark that no connection information was supplied
	 *
	 * @param message
	 * @param cause
	 */
	public NoConnectionInfoException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Generic exception to mark that no connection information was supplied
	 *
	 * @param cause
	 */
	public NoConnectionInfoException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Generic exception to mark that no connection information was supplied
	 *
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public NoConnectionInfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
