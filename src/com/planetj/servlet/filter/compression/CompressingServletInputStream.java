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

import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Implementation of {@link ServletInputStream} which will decompress data
 * read from it.</p>
 *
 * @author Sean Owen
 * @since 1.6
 */
final class CompressingServletInputStream extends ServletInputStream {

    private final InputStream compressingStream;
    private boolean closed;

    CompressingServletInputStream(InputStream rawStream,
            CompressingStreamFactory compressingStreamFactory,
            CompressingFilterContext context) throws IOException {
        this.compressingStream =
                compressingStreamFactory.getCompressingStream(rawStream, context).getCompressingInputStream();
    }

    @Override
    public int read() throws IOException {
        checkClosed();
        return compressingStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        checkClosed();
        return compressingStream.read(b);
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        checkClosed();
        return compressingStream.read(b, offset, length);
    }

    // Leave implementation of readLine() in superclass alone, even if it's not so efficient
    @Override
    public long skip(long n) throws IOException {
        checkClosed();
        return compressingStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        checkClosed();
        return compressingStream.available();
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            compressingStream.close();
            closed = true;
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        checkClosed();
        compressingStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        checkClosed();
        compressingStream.reset();
    }

    @Override
    public boolean markSupported() {
        checkClosed();
        return compressingStream.markSupported();
    }

    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Stream is already closed");
        }
    }

    @Override
    public String toString() {
        return "CompressingServletInputStream";
    }
}
