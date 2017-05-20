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

import com.armineasy.injection.abstractions.GuiceSiteInjectorModule;
import java.util.Comparator;

/**
 * Assists to bind into the injection module from multiple JAR or enterprise archives
 *
 * @author GedMarc
 * @since 12 Dec 2016
 *
 */
public abstract class GuiceSiteBinder implements Comparator<GuiceSiteBinder>, DefaultBinder<GuiceSiteInjectorModule>
{

    /**
     * A default regex to identify query parameters
     */
    protected static String QueryParametersRegex = "(\\?.*)?";
    /**
     * Default order 100
     */
    private int DefaultSortOrder = 100;

    /**
     * Blank constructor
     */
    public GuiceSiteBinder()
    {
        //Nothing needed to be done
    }

    /**
     * The default sort order number is 100
     *
     * @return
     */
    public Integer sortOrder()
    {
        return DefaultSortOrder;
    }

    /**
     * Sets the default sort order
     *
     * @param DefaultSortOrder
     */
    public void setDefaultSortOrder(int DefaultSortOrder)
    {
        this.DefaultSortOrder = DefaultSortOrder;
    }

    /**
     * Compares the items across
     *
     * @param o1
     * @param o2
     *
     * @return
     */
    @Override
    public int compare(GuiceSiteBinder o1, GuiceSiteBinder o2)
    {
        if (o1 == null || o2 == null)
        {
            return -1;
        }
        return o1.sortOrder().compareTo(o2.sortOrder());
    }
}
