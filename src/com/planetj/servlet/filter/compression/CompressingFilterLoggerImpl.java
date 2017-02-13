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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A simple facade in front of logging services -- this class is used by other
 * classes in this package to log informational messages. It simply logs these
 * message to the servlet log.
 *
 * @author Sean Owen
 */
final class CompressingFilterLoggerImpl implements CompressingFilterLogger {

    private static final String MESSAGE_PREFIX = " [CompressingFilter/" + CompressingFilter.VERSION + "] ";
    private final ServletContext servletContext;
    private final boolean debug;
    private final CompressingFilterLogger delegate;

    CompressingFilterLoggerImpl(ServletContext ctx,
            boolean debug,
            String delegateLoggerName,
            boolean isJavaUtilLogger) throws ServletException {
        assert ctx != null;
        servletContext = ctx;
        this.debug = debug;

        if (delegateLoggerName == null) {
            delegate = null;
        } else if (isJavaUtilLogger) {
            delegate = new JavaUtilLoggingImpl(delegateLoggerName);
        } else {
            try {
                // Load by reflection to avoid a hard dependence on Jakarta Commons Logging
                Class<?> delegateClass =
                        Class.forName("com.planetj.servlet.filter.compression.JakartaCommonsLoggingImpl");
                Constructor<?> constructor =
                        delegateClass.getConstructor(String.class);
                delegate = (CompressingFilterLogger) constructor.newInstance(delegateLoggerName);
            } catch (ClassNotFoundException cnfe) {
                throw new ServletException(cnfe);
            } catch (NoSuchMethodException nsme) {
                throw new ServletException(nsme);
            } catch (InvocationTargetException ite) {
                throw new ServletException(ite);
            } catch (IllegalAccessException iae) {
                throw new ServletException(iae);
            } catch (InstantiationException ie) {
                throw new ServletException(ie);
            }
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public void log(String message) {
        servletContext.log(MESSAGE_PREFIX + message);
        if (delegate != null) {
            delegate.log(message);
        }
    }

    public void log(String message, Throwable t) {
        servletContext.log(MESSAGE_PREFIX + message, t);
        if (delegate != null) {
            delegate.log(message, t);
        }
    }

    public void logDebug(String message) {
        if (debug) {
            servletContext.log(MESSAGE_PREFIX + message);
            if (delegate != null) {
                delegate.logDebug(message);
            }
        }
    }

    public void logDebug(String message, Throwable t) {
        if (debug) {
            servletContext.log(MESSAGE_PREFIX + message, t);
            if (delegate != null) {
                delegate.logDebug(message, t);
            }
        }
    }

    @Override
    public String toString() {
        return "CompressingFilterLoggerImpl";
    }
}
