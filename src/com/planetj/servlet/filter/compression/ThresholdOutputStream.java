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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Sean Owen
 */
final class ThresholdOutputStream extends OutputStream {

    private boolean buffering;
    private final OutputStream out1;
    private OutputStream out2;
    private CompressingOutputStream compressingOutputStream;
    private final CompressingStreamFactory compressingStreamFactory;
    private final CompressingFilterContext context;
    private final int threshold;
    private final BufferCommitmentCallback bufferCommitmentCallback;
    private ByteArrayOutputStream buffer;
    private boolean closed;
    private boolean forceOut1;
    private final CompressingFilterLogger logger;

    ThresholdOutputStream(OutputStream out1,
            CompressingStreamFactory compressingStreamFactory,
            CompressingFilterContext context,
            BufferCommitmentCallback thresholdReachedCallback,
            CompressingFilterLogger logger) {
        assert out1 != null && compressingStreamFactory != null
                && context != null && thresholdReachedCallback != null;
        buffering = true;
        this.out1 = out1;
        this.compressingStreamFactory = compressingStreamFactory;
        this.context = context;
        threshold = context.getCompressionThreshold();
        bufferCommitmentCallback = thresholdReachedCallback;
        this.logger = logger;
    }

    @Override
    public void write(int b) throws IOException {
        checkClosed();
        if (forceOut1) {
            out1.write(b);
        } else if (continueBuffering(1)) {
            assert buffering;
            assert buffer != null;
            buffer.write(b);
        } else {
            assert !buffering;
            assert out2 != null;
            out2.write(b);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        checkClosed();
        if (forceOut1) {
            out1.write(b);
        } else if (continueBuffering(b.length)) {
            assert buffering;
            assert buffer != null;
            buffer.write(b);
        } else {
            assert !buffering;
            assert out2 != null;
            out2.write(b);
        }
    }

    @Override
    public void write(byte[] b, int offset, int length) throws IOException {
        checkClosed();
        if (forceOut1) {
            out1.write(b, offset, length);
        } else if (continueBuffering(length)) {
            assert buffering;
            assert buffer != null;
            buffer.write(b, offset, length);
        } else {
            assert !buffering;
            assert out2 != null;
            out2.write(b, offset, length);
        }
    }

    @Override
    public void flush() throws IOException {
        if (forceOut1) {
            out1.flush();
        } else if (!buffering) {
            out2.flush();
        } else {
            logger.logDebug("Switching to alternate stream due to flush()");
            switchToOutputStream2();
        }
    }

    @Override
    public void close() throws IOException {

        closed = true;

        if (forceOut1) {
            out1.flush();
            out1.close();
        } else if (buffering) {
            forceOutputStream1();
            out1.flush();
            out1.close();
        } else {
            assert out2 != null;
            assert compressingOutputStream != null;
            out2.flush();
            compressingOutputStream.finish();
            out2.close();
        }

    }

    void reset() {
        if (forceOut1 || !buffering) {
            throw new IllegalStateException("Can't reset");
        }
        if (buffer != null) {
            buffer.reset();
        }
        // else do nothing -- can't reset anything from here
    }

    @Override
    public String toString() {
        return "ThresholdOutputStream";
    }

    /**
     * Implementations of this interface are used to receive notification that
     * this stream has either committed bytes to the "raw" stream (without
     * compression), or has committed bytes to a compressing stream.
     */
    interface BufferCommitmentCallback {

        void rawStreamCommitted();

        void compressingStreamCommitted();
    }

    private boolean continueBuffering(int numAdditionalBytes) throws IOException {
        boolean shouldContinue = false;
        if (buffering) {
            if (buffer == null) {
                if (numAdditionalBytes >= threshold) {
                    // first write is so big that it would overrun the buffer; don't even create the buffer
                    switchToOutputStream2();
                } else {
                    // allocate the buffer
                    buffer = new ByteArrayOutputStream(threshold);
                    shouldContinue = true;
                }
            } else if (buffer.size() + numAdditionalBytes >= threshold) {
                switchToOutputStream2();
            } else {
                shouldContinue = true;
            }
        }
        return shouldContinue;
    }

    void forceOutputStream1() throws IOException {
        logger.logDebug("Forced to primary stream");
        forceOut1 = true;
        if (bufferCommitmentCallback != null) {
            bufferCommitmentCallback.rawStreamCommitted();
        }
        flushBufferToStream(out1);
    }

    void switchToOutputStream2() throws IOException {
        logger.logDebug("Forced to alternate stream");
        assert buffering;
        // invoke callback
        if (bufferCommitmentCallback != null) {
            bufferCommitmentCallback.compressingStreamCommitted();
        }
        compressingOutputStream = compressingStreamFactory.getCompressingStream(out1, context);
        out2 = compressingOutputStream.getCompressingOutputStream();
        flushBufferToStream(out2);
    }

    private void flushBufferToStream(OutputStream out) throws IOException {
        // flush buffered data to out
        if (buffer != null) {
            buffer.writeTo(out);
            buffer = null;
        }
        buffering = false;
    }

    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Stream is closed");
        }
    }
}
