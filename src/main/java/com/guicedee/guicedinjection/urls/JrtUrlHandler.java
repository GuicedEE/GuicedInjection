package com.guicedee.guicedinjection.urls;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;

/**
 * URL stream handler provider for the {@code jrt} protocol.
 */
public class JrtUrlHandler
		extends URLStreamHandlerProvider
{

	/**
	 * Creates a {@link URLStreamHandler} for the {@code jrt} protocol.
	 *
	 * @param protocol the requested protocol
	 * @return a handler for {@code jrt}, or {@code null} if unsupported
	 */
	@Override
	public URLStreamHandler createURLStreamHandler(String protocol)
	{
		if ("jrt".equals(protocol))
		{
			return new URLStreamHandler()
			{
				@Override
				protected URLConnection openConnection(URL u) throws IOException
				{
					return new JrtUrlConnection(u);
				}
			};
		}
		return null;
	}

}
