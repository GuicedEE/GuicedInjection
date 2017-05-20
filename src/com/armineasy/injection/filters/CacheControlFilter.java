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
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Caches everything until 2020
 *
 * @author GedMarc
 */
@Singleton
public class CacheControlFilter implements Filter
{

    /**
     * Sets the cache control headers
     *
     * @param request
     * @param response
     * @param chain
     *
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = ((HttpServletRequest) request).getRequestURI();
        if (path.toLowerCase().endsWith(".js") || path.toLowerCase().endsWith(".css"))
        {
            resp.setHeader("Expires", "Tue, 03 Jul 2020 06:00:00 GMT");
            resp.setHeader("Cache-Control", "public, max-age=2546787");
        }
        else
        {
            resp.setHeader("Cache-Control", "private, max-age=0");
        }

        chain.doFilter(request, response);
    }

    /**
     * Doesn't do much, or anything
     *
     * @param fc
     *
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig fc) throws ServletException
    {
        //Nothing needed
    }

    @Override
    public void destroy()
    {
        //Nothing needed
    }

}
