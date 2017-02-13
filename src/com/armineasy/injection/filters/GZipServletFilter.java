/* 
 * The MIT License
 *
 * Copyright 2017 Marc Magon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.armineasy.injection.filters;

import com.google.inject.Singleton;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * GZips content in and out
 *
 * @author GedMarc
 */
@Singleton
public class GZipServletFilter implements Filter
{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        //Nothing needed
    }

    @Override
    public void destroy()
    {
        //Nothing needed
    }

    /**
     * GZips data in and out for everything
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException
    {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (acceptsGZipEncoding(httpRequest))
        {
            httpResponse.addHeader("Content-Encoding", "gzip");
            GZipServletResponseWrapper gzipResponse
                    = new GZipServletResponseWrapper(httpResponse);
            chain.doFilter(request, gzipResponse);
            gzipResponse.close();
        }
        else
        {
            chain.doFilter(request, response);
        }
    }

    /**
     * Sets the accept encoding
     *
     * @param httpRequest
     * @return
     */
    private boolean acceptsGZipEncoding(HttpServletRequest httpRequest)
    {
        String acceptEncoding
                = httpRequest.getHeader("Accept-Encoding");

        return acceptEncoding != null
                && acceptEncoding.contains("gzip");
    }
}

/**
 * A response wrapper for servlets
 *
 * @author GedMarc
 */
class GZipServletResponseWrapper extends HttpServletResponseWrapper
{

    private GZipServletOutputStream gzipOutputStream = null;
    private PrintWriter printWriter = null;

    public GZipServletResponseWrapper(HttpServletResponse response)
            throws IOException
    {
        super(response);
    }

    /**
     * Closes the print writer stream
     *
     * @throws IOException
     */
    public void close() throws IOException
    {

        //PrintWriter.close does not throw exceptions.
        //Hence no try-catch block.
        if (this.printWriter != null)
        {
            this.printWriter.close();
        }

        if (this.gzipOutputStream != null)
        {
            this.gzipOutputStream.close();
        }
    }

    /**
     * Flush OutputStream or PrintWriter
     *
     * @throws IOException
     */
    @Override
    public void flushBuffer() throws IOException
    {

        //PrintWriter.flush() does not throw exception
        if (this.printWriter != null)
        {
            this.printWriter.flush();
        }

        IOException exception1 = null;
        try
        {
            if (this.gzipOutputStream != null)
            {
                this.gzipOutputStream.flush();
            }
        }
        catch (IOException e)
        {
            exception1 = e;
        }

        IOException exception2 = null;
        try
        {
            super.flushBuffer();
        }
        catch (IOException e)
        {
            exception2 = e;
        }

        if (exception1 != null)
        {
            throw exception1;
        }
        if (exception2 != null)
        {
            throw exception2;
        }
    }

    /**
     * Returns an output stream of GZIP
     *
     * @return
     * @throws IOException
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        if (this.printWriter != null)
        {
            throw new IllegalStateException(
                    "PrintWriter obtained already - cannot get OutputStream");
        }
        if (this.gzipOutputStream == null)
        {
            this.gzipOutputStream = new GZipServletOutputStream(
                    getResponse().getOutputStream());
        }
        return this.gzipOutputStream;
    }

    /**
     * Writes the GZIP
     *
     * @return
     * @throws IOException
     */
    @Override
    public PrintWriter getWriter() throws IOException
    {
        if (this.printWriter == null && this.gzipOutputStream != null)
        {
            throw new IllegalStateException(
                    "OutputStream obtained already - cannot get PrintWriter");
        }
        if (this.printWriter == null)
        {
            this.gzipOutputStream = new GZipServletOutputStream(
                    getResponse().getOutputStream());
            this.printWriter = new PrintWriter(new OutputStreamWriter(
                    this.gzipOutputStream, getResponse().getCharacterEncoding()));
        }
        return this.printWriter;
    }

    @Override
    public void setContentLength(int len)
    {
        //ignore, since content length of zipped content
        //does not match content length of unzipped content.
    }
}

/**
 * Handles the GZIP output
 *
 * @author GedMarc
 */
class GZipServletOutputStream extends ServletOutputStream
{

    private GZIPOutputStream gzipOutputStream = null;

    public GZipServletOutputStream(OutputStream output)
            throws IOException
    {
        super();
        this.gzipOutputStream = new GZIPOutputStream(output);
    }

    @Override
    public void close() throws IOException
    {
        this.gzipOutputStream.close();
    }

    @Override
    public void flush() throws IOException
    {
        this.gzipOutputStream.flush();
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        this.gzipOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        this.gzipOutputStream.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException
    {
        this.gzipOutputStream.write(b);
    }
}
