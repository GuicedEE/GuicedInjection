package com.guicedee.guicedinjection.urls;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;

public class JrtUrlHandler
		extends URLStreamHandlerProvider
{

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
