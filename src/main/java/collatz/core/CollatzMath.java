package collatz.core;

import collatz.model.ComparisonSummary;
import collatz.model.RangeRow;
import collatz.model.SequenceAnalysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Collatz (3n+1) dynamics: no console or user interaction.
 */
public final class CollatzMath {

    /** Largest starting value guaranteed not to overflow on the first odd step (3n+1). */
    public static final long MAX_SAFE_START = (Long.MAX_VALUE - 1) / 3;

    /** Hard cap on iterations to avoid infinite loops if something goes wrong. */
    private static final int MAX_ITERATIONS = 5_000_000;

    /** Default cap on stored trajectory length (memory / display). */
    public static final int DEFAULT_MAX_TRAJECTORY_TERMS = 10_000;

    /** Maximum inclusive span for range mode (from..to). */
    public static final int MAX_RANGE_WIDTH = 2_000;

    public record AnalysisOptions(boolean includeTrajectory, int maxTrajectoryTerms) {
        public AnalysisOptions {
            if (maxTrajectoryTerms < 0) {
                throw new IllegalArgumentException("maxTrajectoryTerms must be non-negative");
            }
        }

        public static AnalysisOptions statsOnly() {
            return new AnalysisOptions(false, 0);
        }

        public static AnalysisOptions withTrajectory(int maxTerms) {
            return new AnalysisOptions(true, maxTerms);
        }
    }

    private CollatzMath() {
    }

    /**
     * Single Collatz step: n/2 if even, else 3n+1 (throws if 3n+1 overflows {@code long}).
     */
    public static long successor(long n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        if ((n & 1L) == 0L) {
            return n / 2;
        }
        if (n > MAX_SAFE_START) {
            throw new ArithmeticException("3n+1 would overflow long for n=" + n);
        }
        return 3 * n + 1;
    }

    /**
     * Whether {@code n} is safe as a starting value for full analysis without overflow on the first odd step.
     */
    public static boolean isSafeStart(long n) {
        return n >= 1 && n <= MAX_SAFE_START;
    }

    /**
     * Full analysis from {@code start} until 1.
     */
    public static SequenceAnalysis analyze(long start, AnalysisOptions options) {
        if (start < 1) {
            throw new IllegalArgumentException("Starting value must be at least 1");
        }
        if (!isSafeStart(start)) {
            throw new ArithmeticException(
                    "Starting value too large for safe 64-bit Collatz (max safe start: " + MAX_SAFE_START + ")"
            );
        }

        boolean wantTrajectory = options.includeTrajectory();
        int cap = wantTrajectory
                ? Math.min(
                        options.maxTrajectoryTerms() == 0 ? DEFAULT_MAX_TRAJECTORY_TERMS : options.maxTrajectoryTerms(),
                        DEFAULT_MAX_TRAJECTORY_TERMS)
                : 0;

        List<Long> trajectory = wantTrajectory ? new ArrayList<>(Math.min(cap, 256)) : null;
        long n = start;
        long peak = n;
        int steps = 0;
        boolean truncated = false;

        if (trajectory != null) {
            trajectory.add(n);
        }

        while (n != 1) {
            if (steps >= MAX_ITERATIONS) {
                throw new IllegalStateException(
                        "Stopped after " + MAX_ITERATIONS + " steps (unexpected for typical Collatz exploration)"
                );
            }
            n = successor(n);
            steps++;
            if (n > peak) {
                peak = n;
            }
            if (trajectory != null) {
                if (trajectory.size() < cap) {
                    trajectory.add(n);
                } else if (n != 1) {
                    truncated = true;
                }
            }
        }

        if (trajectory == null) {
            return SequenceAnalysis.emptyTrajectory(start, steps, peak);
        }
        return new SequenceAnalysis(start, steps, peak, trajectory, truncated);
    }

    /**
     * Scan each starting value in {@code [from, to]} (inclusive). Uses stats-only analysis.
     */
    public static List<RangeRow> analyzeRange(long from, long to) {
        if (from < 1 || to < 1) {
            throw new IllegalArgumentException("Range endpoints must be at least 1");
        }
        if (from > to) {
            throw new IllegalArgumentException("From must be less than or equal to to");
        }
        long width = to - from + 1;
        if (width > MAX_RANGE_WIDTH) {
            throw new IllegalArgumentException(
                    "Range too wide (max " + MAX_RANGE_WIDTH + " values). Narrow the interval."
            );
        }
        if (to > MAX_SAFE_START) {
            throw new ArithmeticException("Upper bound exceeds safe 64-bit Collatz limit: " + MAX_SAFE_START);
        }

        List<RangeRow> rows = new ArrayList<>((int) width);
        for (long s = from; s <= to; s++) {
            SequenceAnalysis a = analyze(s, AnalysisOptions.statsOnly());
            rows.add(new RangeRow(s, a.totalSteps(), a.peakValue()));
        }
        return rows;
    }

    /**
     * Compare two trajectories step-by-step in parallel: same index = same number of map applications from each start.
     * Returns the first index where the two values differ, or -1 if they stay equal until both reach 1.
     */
    public static ComparisonSummary compare(long firstStart, long secondStart) {
        SequenceAnalysis first = analyze(firstStart, AnalysisOptions.statsOnly());
        SequenceAnalysis second = analyze(secondStart, AnalysisOptions.statsOnly());

        long a = firstStart;
        long b = secondStart;
        int index = 0;
        while (true) {
            if (a != b) {
                return new ComparisonSummary(first, second, index);
            }
            if (a == 1) {
                return new ComparisonSummary(first, second, -1);
            }
            a = successor(a);
            b = successor(b);
            index++;
        }
    }
}
