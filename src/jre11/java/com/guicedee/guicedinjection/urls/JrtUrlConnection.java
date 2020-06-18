package com.guicedee.guicedinjection.urls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public class JrtUrlConnection
		extends URLConnection
{
	private Path path;


	protected JrtUrlConnection(URL url)
	{
		super(url);
		URI uri = URI.create(url.toString()
		                        .replace("jar:jrt:/", "jrt:/")
		                        .replace("!", ""));
		path = Path.of(uri);
	}

	@Override
	public void connect() throws IOException
	{
	}

	@Override
	public int getContentLength()
	{
		return (int) path.toFile()
		                 .length();
	}

	@Override
	public long getContentLengthLong()
	{
		return path.toFile()
		           .length();
	}

	@Override
	public long getDate()
	{
		return path.toFile()
		           .lastModified();
	}

	@Override
	public long getLastModified()
	{
		return path.toFile()
		           .lastModified();
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return Files.newInputStream(path);
	}

	@Override
	public OutputStream getOutputStream() throws IOException
	{
		return Files.newOutputStream(path);
	}
}
