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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link CompressingFilterLogger} implementation based on java.util.logging.
 *
 * @author Sean Owen
 */
public final class JavaUtilLoggingImpl implements CompressingFilterLogger {

    private final Logger logger;

    /**
     * This constructor is public so that it may be instantiated by reflection.
     *
     * @param loggerName {@link Logger} name
     */
    public JavaUtilLoggingImpl(String loggerName) {
        logger = Logger.getLogger(loggerName);
    }

    public void log(String message) {
        logger.info(message);
    }

    public void log(String message, Throwable t) {
        logger.log(Level.INFO, message, t);
    }

    public void logDebug(String message) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(message);
        }
    }

    public void logDebug(String message, Throwable t) {
        logger.log(Level.FINE, message, t);
    }

    public boolean isDebug() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public String toString() {
        return "JavaUtilLoggingImpl[" + logger + ']';
    }
}
