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

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Implementation of {@link ServletOutputStream} which will optionally compress
 * data written to it.
 *
 * @author Sean Owen
 */
final class CompressingServletOutputStream extends ServletOutputStream {

    private final OutputStream rawStream;
    private final CompressingStreamFactory compressingStreamFactory;
    private final CompressingHttpServletResponse compressingResponse;
    private final CompressingFilterContext context;
    private ThresholdOutputStream thresholdOutputStream;
    private boolean closed;
    private boolean aborted;
    private final CompressingFilterLogger logger;

    CompressingServletOutputStream(OutputStream rawStream,
            CompressingStreamFactory compressingStreamFactory,
            CompressingHttpServletResponse compressingResponse,
            CompressingFilterContext context,
            CompressingFilterLogger logger) {
        this.rawStream = rawStream;
        this.compressingStreamFactory = compressingStreamFactory;
        this.compressingResponse = compressingResponse;
        this.context = context;
        this.logger = logger;
        closed = false;
        aborted = false;
    }

    @Override
    public void write(byte[] b) throws IOException {
        checkClosed();
        checkWriteState();
        assert thresholdOutputStream != null;
        thresholdOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int offset, int length) throws IOException {
        checkClosed();
        checkWriteState();
        assert thresholdOutputStream != null;
        thresholdOutputStream.write(b, offset, length);
    }

    @Override
    public void write(int b) throws IOException {
        checkClosed();
        checkWriteState();
        assert thresholdOutputStream != null;
        thresholdOutputStream.write(b);
    }

    @Override
    public void flush() {
        // do nothing actually
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            compressingResponse.flushBuffer();
            closed = true;
            if (thresholdOutputStream == null) {
                // Nothing written, so, signal that effectively the 'raw' output stream was used and close it
                compressingResponse.rawStreamCommitted();
                rawStream.close();
            } else {
                thresholdOutputStream.close();
            }
        }
    }

    @Override
    public String toString() {
        return "CompressingServletOutputStream";
    }

    boolean isClosed() {
        return closed;
    }

    void reset() {
        // can't reset rawStream, so do nothing if compressionDisabled, else:
        if (thresholdOutputStream != null) {
            thresholdOutputStream.reset();
        }
    }

    void engageCompression() throws IOException {
        checkWriteState();
        thresholdOutputStream.switchToOutputStream2();
    }

    void abortCompression() throws IOException {
        assert thresholdOutputStream == null;
        // remember that this was called, in case thresholdOutputStream has not been set up yet,
        // so that when it is we can invoke forceOutputStream1()
        checkWriteState();
        thresholdOutputStream.forceOutputStream1();
        aborted = true;
    }

    boolean isAborted() {
        return aborted;
    }

    private void checkWriteState() {
        if (thresholdOutputStream == null) {
            thresholdOutputStream =
                    new ThresholdOutputStream(rawStream,
                    compressingStreamFactory,
                    context,
                    new ResponseBufferCommitmentCallback(compressingResponse),
                    logger);
        }
    }

    private void checkClosed() throws IOException {
        if (closed) {
            throw new IOException("Stream is already closed");
        }
    }

    private static final class ResponseBufferCommitmentCallback
            implements ThresholdOutputStream.BufferCommitmentCallback {

        private final CompressingHttpServletResponse response;

        private ResponseBufferCommitmentCallback(CompressingHttpServletResponse response) {
            assert response != null;
            this.response = response;
        }

        public void rawStreamCommitted() {
            response.rawStreamCommitted();
        }

        public void compressingStreamCommitted() {
            response.switchToCompression();
        }

        @Override
        public String toString() {
            return "ResponseBufferCommitmentCallback";
        }
    }
}
