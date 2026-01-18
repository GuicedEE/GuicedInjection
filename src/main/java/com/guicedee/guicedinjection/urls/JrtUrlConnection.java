package com.guicedee.guicedinjection.urls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * URLConnection for {@code jrt:} URLs, normalizing {@code jar:jrt:/} variants.
 */
public class JrtUrlConnection
		extends URLConnection
{
	private Path path;
	
	/**
	 * Cleans up incorrectly found jar:jrt urls and returns a viable URI
	 *
	 * @param url the original URL to normalize
	 */
	protected JrtUrlConnection(URL url)
	{
		super(url);
		URI uri = URI.create(url.toString()
		                        .replace("jar:jrt:/", "jrt:/")
		                        .replace("!", ""));
		path = Path.of(uri);
	}

	/**
	 * No-op connect, as the path is resolved on construction.
	 */
	@Override
	public void connect() throws IOException
	{
	}

	/**
	 * Returns the content length of the resolved path.
	 *
	 * @return the content length in bytes
	 */
	@Override
	public int getContentLength()
	{
		return (int) path.toFile()
		                 .length();
	}

	/**
	 * Returns the content length of the resolved path.
	 *
	 * @return the content length in bytes
	 */
	@Override
	public long getContentLengthLong()
	{
		return path.toFile()
		           .length();
	}

	/**
	 * Returns the last modified time of the resolved path.
	 *
	 * @return the last modified time in milliseconds since epoch
	 */
	@Override
	public long getDate()
	{
		return path.toFile()
		           .lastModified();
	}

	/**
	 * Returns the last modified time of the resolved path.
	 *
	 * @return the last modified time in milliseconds since epoch
	 */
	@Override
	public long getLastModified()
	{
		return path.toFile()
		           .lastModified();
	}

	/**
	 * Opens an input stream to the resolved path.
	 *
	 * @return an input stream for the resolved path
	 */
	@Override
	public InputStream getInputStream() throws IOException
	{
		return Files.newInputStream(path);
	}

	/**
	 * Opens an output stream to the resolved path.
	 *
	 * @return an output stream for the resolved path
	 */
	@Override
	public OutputStream getOutputStream() throws IOException
	{
		return Files.newOutputStream(path);
	}
}
