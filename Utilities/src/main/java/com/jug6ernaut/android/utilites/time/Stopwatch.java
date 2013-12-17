package com.jug6ernaut.android.utilites.time;

import java.util.concurrent.TimeUnit;

import static com.jug6ernaut.android.utilites.time.Preconditions.checkNotNull;
import static com.jug6ernaut.android.utilites.time.Preconditions.checkState;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class Stopwatch {
        private final Ticker ticker;
        private boolean isRunning;
        private long elapsedNanos;
        private long startTick;

        /**
         * Creates (but does not start) a new stopwatch using {@link System#nanoTime}
         * as its time source.
         */
        public Stopwatch() {
            this(Ticker.systemTicker());
        }

        /**
         * Creates (but does not start) a new stopwatch, using the specified time
         * source.
         */
        public Stopwatch(Ticker ticker) {
            this.ticker = checkNotNull(ticker);
        }

        /**
         * Returns {@code true} if {@link #start()} has been called on this stopwatch,
         * and {@link #stop()} has not been called since the last call to {@code
         * start()}.
         */
        public boolean isRunning() {
            return isRunning;
        }

        /**
         * Starts the stopwatch.
         *
         * @throws IllegalStateException if the stopwatch is already running.
         */
        public Stopwatch start() {
            checkState(!isRunning);
            isRunning = true;
            startTick = ticker.read();
            return this;
        }

        /**
         * Stops the stopwatch. Future reads will return the fixed duration that had
         * elapsed up to this point.
         *
         * @throws IllegalStateException if the stopwatch is already stopped.
         */
        public Stopwatch stop() {
            long tick = ticker.read();
            checkState(isRunning);
            isRunning = false;
            elapsedNanos += tick - startTick;
            return this;
        }

        /**
         * Sets the elapsed time for this stopwatch to zero,
         * and places it in a stopped state.
         */
        public Stopwatch reset() {
            elapsedNanos = 0;
            isRunning = false;
            return this;
        }

        private long elapsedNanos() {
            return isRunning ? ticker.read() - startTick + elapsedNanos : elapsedNanos;
        }

        /**
         * Returns the current elapsed time shown on this stopwatch, expressed
         * in the desired time unit, with any fraction rounded down.
         *
         * <p>Note that the overhead of measurement can be more than a microsecond, so
         * it is generally not useful to specify {@link TimeUnit#NANOSECONDS}
         * precision here.
         */
        public long elapsedTime(TimeUnit desiredUnit) {
            return desiredUnit.convert(elapsedNanos(), NANOSECONDS);
        }

        /**
         * Returns the current elapsed time shown on this stopwatch, expressed
         * in milliseconds, with any fraction rounded down. This is identical to
         * {@code elapsedTime(TimeUnit.MILLISECONDS}.
         */
        public long elapsedMillis() {
            return elapsedTime(MILLISECONDS);
        }

        /**
         * Returns a string representation of the current elapsed time; equivalent to
         * {@code toString(4)} (four significant figures).
         */
        @Override public String toString() {
            return toString(4);
        }

        /**
         * Returns a string representation of the current elapsed time, choosing an
         * appropriate unit and using the specified number of significant figures.
         * For example, at the instant when {@code elapsedTime(NANOSECONDS)} would
         * return {1234567}, {@code toString(4)} returns {@code "1.235 ms"}.
         */
        public String toString(int significantDigits) {
            long nanos = elapsedNanos();

            TimeUnit unit = chooseUnit(nanos);
            double value = (double) nanos / NANOSECONDS.convert(1, unit);

            // Too bad this functionality is not exposed as a regular method call
            return String.format("%." + significantDigits + "g %s",
                    value, abbreviate(unit));
        }

        private static TimeUnit chooseUnit(long nanos) {
            if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
                return SECONDS;
            }
            if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
                return MILLISECONDS;
            }
            if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
                return MICROSECONDS;
            }
            return NANOSECONDS;
        }

        private static String abbreviate(TimeUnit unit) {
            switch (unit) {
                case NANOSECONDS:
                    return "ns";
                case MICROSECONDS:
                    return "\u03bcs"; // Î¼s
                case MILLISECONDS:
                    return "ms";
                case SECONDS:
                    return "s";
                default:
                    throw new AssertionError();
            }
        }

//    long startMarker=0;
//    long total=0;
//    long lap  =0;
//    List<String> laps = new ArrayList<String>();
//
//    public Stopwatch (){
//        start();
//    }
//
//    public void start(){
//        total = 0;
//        startMarker = System.currentTimeMillis();
//        laps.clear();
//    }
//
//    public long stop(){
//        lap = (System.currentTimeMillis() - startMarker);
//        total += lap;
//        return lap;
//    }
//
//    public long lap(String lapName){
//        total += stop();
//        laps.add(lapName + ": " + toString());
//        startMarker = System.currentTimeMillis();
//        return lap;
//    }
//
//    public long lap(){
//        return lap("*");
//    }
//
//    public long getLap(){
//        return lap;
//    }
//
//    public long getTotal(){
//        return total;
//    }
//
//    public List<String> getLaps(){
//        return laps;
//    }
//
//    public String toString(){
//        return TimeUtils.milliToString(lap);
//    }
//
//    public String toString(long nano){
//        return TimeUtils.milliToString(nano);
//    }

}
