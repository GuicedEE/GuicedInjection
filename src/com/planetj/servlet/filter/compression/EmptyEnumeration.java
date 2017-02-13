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

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * <p>
 * An {@link Enumeration} which enumerates nothing.</p>
 *
 * @author Sean Owen
 * @since 1.6
 */
final class EmptyEnumeration implements Enumeration<String>
{

    private static final Enumeration<String> instance = new EmptyEnumeration();

    private EmptyEnumeration()
    {
        // do nothing
    }

    public static Enumeration<String> getInstance()
    {
        return instance;
    }

    /**
     * @return false always
     */
    public boolean hasMoreElements()
    {
        return false;
    }

    /**
     * @throws NoSuchElementException always
     */
    public String nextElement()
    {
        throw new NoSuchElementException();
    }

    @Override
    public String toString()
    {
        return "EmptyEnumeration";
    }
}
