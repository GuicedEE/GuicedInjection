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

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>This class provides runtime statistics on the performance of
 * {@link CompressingFilter}. If stats are enabled, then an instance of this
 * object will be available in the servlet context under the key
 * {@link #STATS_KEY}. It can be retrieved and used like so:</p>
 * <p/>
 * <
 * pre>
 * ServletContext ctx = ...;
 * // in a JSP, "ctx" is already available as the "application" variable
 * CompressingFilterStats stats = (CompressingFilterStats) ctx.getAttribute(CompressingFilterStats.STATS_KEY);
 * double ratio = stats.getAverageCompressionRatio();
 * ...
 * </pre>
 *
 * @author Sean Owen
 * @since 1.1
 */
public final class CompressingFilterStats implements Serializable {

    private static final long serialVersionUID = -2246829834191152845L;
    /**
     * Key under which a {@link CompressingFilterStats} object can be found in
     * the servlet context.
     */
    public static final String STATS_KEY = "com.planetj.servlet.filter.compression.CompressingFilterStats";
    /**
     * @serial
     */
    private AtomicInteger numResponsesCompressed = new AtomicInteger();
    /**
     * @serial
     */
    private AtomicInteger totalResponsesNotCompressed = new AtomicInteger();
    /**
     * @serial
     */
    private AtomicLong responseInputBytes = new AtomicLong();
    /**
     * @serial
     */
    private AtomicLong responseCompressedBytes = new AtomicLong();
    /**
     * @serial
     */
    private AtomicInteger numRequestsCompressed = new AtomicInteger();
    /**
     * @serial
     */
    private AtomicInteger totalRequestsNotCompressed = new AtomicInteger();
    /**
     * @serial
     */
    private AtomicLong requestInputBytes = new AtomicLong();
    /**
     * @serial
     */
    private AtomicLong requestCompressedBytes = new AtomicLong();
    /**
     * @serial
     */
    private final OutputStatsCallback responseInputStatsCallback;
    /**
     * @serial
     */
    private final OutputStatsCallback responseCompressedStatsCallback;
    /**
     * @serial
     */
    private final InputStatsCallback requestInputStatsCallback;
    /**
     * @serial
     */
    private final InputStatsCallback requestCompressedStatsCallback;

    CompressingFilterStats() {
        responseInputStatsCallback = new OutputStatsCallback(StatsField.RESPONSE_INPUT_BYTES);
        responseCompressedStatsCallback = new OutputStatsCallback(StatsField.RESPONSE_COMPRESSED_BYTES);
        requestInputStatsCallback = new InputStatsCallback(StatsField.REQUEST_INPUT_BYTES);
        requestCompressedStatsCallback = new InputStatsCallback(StatsField.REQUEST_COMPRESSED_BYTES);
    }

    /**
     * @return the number of responses which {@link CompressingFilter} has
     * compressed.
     */
    public int getNumResponsesCompressed() {
        return numResponsesCompressed.get();
    }

    void incrementNumResponsesCompressed() {
        numResponsesCompressed.incrementAndGet();
    }

    /**
     * @return the number of responses which {@link CompressingFilter} has
     * processed but <em>not</em> compressed for some reason (compression not
     * supported by the browser, for example).
     */
    public int getTotalResponsesNotCompressed() {
        return totalResponsesNotCompressed.get();
    }

    void incrementTotalResponsesNotCompressed() {
        totalResponsesNotCompressed.incrementAndGet();
    }

    /**
     * @deprecated use {@link #getResponseInputBytes()}
     */
    @Deprecated
    public long getInputBytes() {
        return responseInputBytes.get();
    }

    /**
     * @return total number of bytes written to the {@link CompressingFilter} in
     * responses.
     */
    public long getResponseInputBytes() {
        return responseInputBytes.get();
    }

    /**
     * @deprecated use {@link #getResponseCompressedBytes()}
     */
    @Deprecated
    public long getCompressedBytes() {
        return responseCompressedBytes.get();
    }

    /**
     * @return total number of compressed bytes written by the
     * {@link CompressingFilter} to the client in responses.
     */
    public long getResponseCompressedBytes() {
        return responseCompressedBytes.get();
    }

    /**
     * @deprecated use {@link #getResponseAverageCompressionRatio()}
     */
    @Deprecated
    public double getAverageCompressionRatio() {
        return getResponseAverageCompressionRatio();
    }

    /**
     * @return average compression ratio (input bytes / compressed bytes) in
     * responses, or 0 if nothing has yet been compressed. Note that this is
     * (typically) greater than 1, not less than 1.
     */
    public double getResponseAverageCompressionRatio() {
        return responseCompressedBytes.get() == 0L ? 0.0 :
                (double) responseInputBytes.get() / (double) responseCompressedBytes.get();
    }

    /**
     * @return the number of requests which {@link CompressingFilter} has
     * compressed.
     * @since 1.6
     */
    public int getNumRequestsCompressed() {
        return numRequestsCompressed.get();
    }

    void incrementNumRequestsCompressed() {
        numRequestsCompressed.incrementAndGet();
    }

    /**
     * @return the number of requests which {@link CompressingFilter} has
     * processed but <em>not</em> compressed for some reason (no compression
     * requested, for example).
     * @since 1.6
     */
    public int getTotalRequestsNotCompressed() {
        return totalRequestsNotCompressed.get();
    }

    void incrementTotalRequestsNotCompressed() {
        totalRequestsNotCompressed.incrementAndGet();
    }

    /**
     * @return total number of bytes written to the {@link CompressingFilter} in
     * requests.
     * @since 1.6
     */
    public long getRequestInputBytes() {
        return requestInputBytes.get();
    }

    /**
     * @return total number of compressed bytes written by the
     * {@link CompressingFilter} to the client in requests.
     * @since 1.6
     */
    public long getRequestCompressedBytes() {
        return requestCompressedBytes.get();
    }

    /**
     * @return average compression ratio (input bytes / compressed bytes) in
     * requests, or 0 if nothing has yet been compressed. Note that this is
     * (typically) greater than 1, not less than 1.
     * @since 1.6
     */
    public double getRequestAverageCompressionRatio() {
        return requestCompressedBytes.get() == 0L ? 0.0 :
                (double) requestInputBytes.get() / (double) requestCompressedBytes.get();
    }

    /**
     * @return a summary of the stats in String form
     */
    @Override
    public String toString() {
        return "CompressingFilterStats[responses compressed: " + numResponsesCompressed
                + ", avg. response compression ratio: " + getResponseAverageCompressionRatio()
                + ", requests compressed: " + numRequestsCompressed
                + ", avg. request compression ratio: " + getRequestAverageCompressionRatio() + ']';
    }

    OutputStatsCallback getOutputStatsCallback(StatsField field) {
        switch (field) {
            case RESPONSE_INPUT_BYTES:
                return responseInputStatsCallback;
            case RESPONSE_COMPRESSED_BYTES:
                return responseCompressedStatsCallback;
            default:
                throw new IllegalArgumentException();
        }
    }

    InputStatsCallback getInputStatsCallback(StatsField field) {
        switch (field) {
            case REQUEST_INPUT_BYTES:
                return requestInputStatsCallback;
            case REQUEST_COMPRESSED_BYTES:
                return requestCompressedStatsCallback;
            default:
                throw new IllegalArgumentException();
        }
    }

    final class OutputStatsCallback implements StatsOutputStream.StatsCallback, Serializable {

        private static final long serialVersionUID = -4483355731273629325L;
        /**
         * @serial
         */
        private final StatsField field;

        private OutputStatsCallback(StatsField field) {
            this.field = field;
        }

        public void bytesWritten(int numBytes) {
            assert numBytes >= 0;
            switch (field) {
                case RESPONSE_INPUT_BYTES:
                    responseInputBytes.addAndGet(numBytes);
                    break;
                case RESPONSE_COMPRESSED_BYTES:
                    responseCompressedBytes.addAndGet(numBytes);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public String toString() {
            return "OutputStatsCallback[field: " + field + ']';
        }
    }

    final class InputStatsCallback implements StatsInputStream.StatsCallback, Serializable {

        private static final long serialVersionUID = 8205059279453932247L;
        /**
         * @serial
         */
        private final StatsField field;

        private InputStatsCallback(StatsField field) {
            this.field = field;
        }

        public void bytesRead(int numBytes) {
            assert numBytes >= 0;
            switch (field) {
                case REQUEST_INPUT_BYTES:
                    requestInputBytes.addAndGet(numBytes);
                    break;
                case REQUEST_COMPRESSED_BYTES:
                    requestCompressedBytes.addAndGet(numBytes);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public String toString() {
            return "InputStatsCallback[field: " + field + ']';
        }
    }

    /**
     * <p>A simple enum used by {@link OutputStatsCallback} to select a field in
     * this class. This is getting a little messy but somehow better than
     * defining a bunch of inner classes?</p>
     *
     * @since 1.6
     */
    enum StatsField implements Serializable {

        RESPONSE_INPUT_BYTES,
        RESPONSE_COMPRESSED_BYTES,
        REQUEST_INPUT_BYTES,
        REQUEST_COMPRESSED_BYTES
    }
}
