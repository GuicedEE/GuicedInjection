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

import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link InputStream} that decorates another {@link InputStream} and notes
 * when bytes are read from the stream. Callers create an instance of
 * {@link StatsInputStream} with an instance of {@link StatsCallback}, which
 * receives notification of reads. This information might be used to tally the
 * number of bytes read from a stream.
 *
 * @author Sean Owen
 * @since 1.6
 */
final class StatsInputStream extends InputStream {

    private final InputStream inputStream;
    private final StatsCallback statsCallback;

    StatsInputStream(InputStream inputStream, StatsCallback statsCallback) {
        assert inputStream != null && statsCallback != null;
        this.inputStream = inputStream;
        this.statsCallback = statsCallback;
    }

    @Override
    public int read() throws IOException {
        int result = inputStream.read();
        if (result >= 0) {
            // here, result is the byte read, or -1 if EOF
            statsCallback.bytesRead(1);
        }
        return result;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int result = inputStream.read(b);
        if (result >= 0) {
            // here, result is number of bytes read
            statsCallback.bytesRead(result);
        }
        return result;
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        int result = inputStream.read(b, offset, length);
        if (result >= 0) {
            // here, result is number of bytes read			
            statsCallback.bytesRead(result);
        }
        return result;
    }

    // Leave implementation of readLine() in superclass alone, even if it's not so efficient
    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public String toString() {
        return "StatsInputStream[" + inputStream + ']';
    }

    interface StatsCallback {

        void bytesRead(int numBytes);
    }
}
