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
package com.planetj.servlet.filter.compression;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * <p>
 * Implementation of {@link HttpServletRequest} which can decompress request bodies that have been compressed.</p>
 *
 * @author Sean Owen
 * @since 1.6
 */
final class CompressedHttpServletRequest extends HttpServletRequestWrapper
{

    private final ServletRequest httpRequest;
    private final CompressingStreamFactory compressingStreamFactory;
    private final CompressingFilterContext context;
    private CompressingServletInputStream compressedSIS;
    private BufferedReader bufferedReader;
    private boolean isGetInputStreamCalled;
    private boolean isGetReaderCalled;

    CompressedHttpServletRequest(HttpServletRequest httpRequest,
            CompressingStreamFactory compressingStreamFactory,
            CompressingFilterContext context)
    {
        super(httpRequest);
        this.httpRequest = httpRequest;
        this.compressingStreamFactory = compressingStreamFactory;
        this.context = context;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        if (isGetReaderCalled)
        {
            throw new IllegalStateException("getReader() has already been called");
        }
        isGetInputStreamCalled = true;
        return getCompressingServletInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        if (isGetInputStreamCalled)
        {
            throw new IllegalStateException("getInputStream() has already been called");
        }
        isGetReaderCalled = true;
        if (bufferedReader == null)
        {
            bufferedReader = new BufferedReader(new InputStreamReader(getCompressingServletInputStream(),
                    getCharacterEncoding()));
        }
        return bufferedReader;
    }

    private CompressingServletInputStream getCompressingServletInputStream() throws IOException
    {
        if (compressedSIS == null)
        {
            compressedSIS = new CompressingServletInputStream(httpRequest.getInputStream(),
                    compressingStreamFactory,
                    context);
        }
        return compressedSIS;
    }

    // Header-related methods -- need to make sure we consume and hide the
    // Content-Encoding header. What a lot of work to get that done:
    private static boolean isFilteredHeader(String headerName)
    {
        // Filter Content-Encoding since we're handing decompression ourselves;
        // filter Accept-Encoding so that downstream services don't try to compress too
        return CompressingHttpServletResponse.CONTENT_ENCODING_HEADER.equalsIgnoreCase(headerName)
                || CompressingHttpServletResponse.ACCEPT_ENCODING_HEADER.equalsIgnoreCase(headerName);
    }

    @Override
    public String getHeader(String header)
    {
        return isFilteredHeader(header) ? null : super.getHeader(header);
    }

    @Override
    public Enumeration<String> getHeaders(String header)
    {
        Enumeration<String> original = super.getHeaders(header);
        if (original == null)
        {
            return null; // match container's behavior exactly in this case
        }
        return isFilteredHeader(header) ? EmptyEnumeration.getInstance() : original;
    }

    @Override
    public long getDateHeader(String header)
    {
        return isFilteredHeader(header) ? -1L : super.getDateHeader(header);
    }

    @Override
    public int getIntHeader(String header)
    {
        return isFilteredHeader(header) ? -1 : super.getIntHeader(header);
    }

    @Override
    public Enumeration<String> getHeaderNames()
    {
        Enumeration<String> original = super.getHeaderNames();
        if (original == null)
        {
            return null; // match container's behavior exactly in this case
        }
        Collection<String> headerNames = new ArrayList<String>();
        while (original.hasMoreElements())
        {
            String headerName = (String) original.nextElement();
            if (!isFilteredHeader(headerName))
            {
                headerNames.add(headerName);
            }
        }
        return new IteratorEnumeration(headerNames.iterator());
    }

    @Override
    public String toString()
    {
        return "CompressedHttpServletRequest";
    }
}
