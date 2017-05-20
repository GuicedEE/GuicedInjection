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
package com.armineasy.injection.interfaces;

import java.util.Comparator;

/**
 * Maps URL's to Servlet classes across multiple JAR's and/or enterprise archives
 *
 * @author GedMarc
 * @since 12 Dec 2016
 *
 */
public abstract class GuiceDefaultBinder
        implements Comparator<GuiceDefaultBinder>, DefaultBinder<com.armineasy.injection.abstractions.GuiceInjectorModule>
{

    private static final int DefaultSortOrder = 100;

    /**
     * Blank constructor
     */
    public GuiceDefaultBinder()
    {
        //Nothing needed to do on constructions
    }

    /**
     * The default value is 100
     *
     * @return
     */
    public Integer sortOrder()
    {
        return DefaultSortOrder;
    }

    @Override
    public int compare(GuiceDefaultBinder o1, GuiceDefaultBinder o2)
    {
        if (o1 == null || o2 == null)
        {
            return -1;
        }
        return o1.sortOrder().compareTo(o2.sortOrder());
    }

}
