package collatz.app;

import collatz.core.CollatzMath;
import collatz.model.ComparisonSummary;
import collatz.model.RangeRow;
import collatz.model.SequenceAnalysis;

import java.util.List;

/**
 * Formatted console output for exploration results.
 */
public final class ConsoleView {

    private static final int LINE_WIDTH = 56;
    private static final char RULE_HEAVY = '=';
    private static final char RULE_LIGHT = '-';

    private ConsoleView() {
    }

    public static void printBanner() {
        printLine(RULE_HEAVY);
        System.out.println("  Collatz explorer - 3n + 1 dynamics (console demo)");
        printLine(RULE_LIGHT);
        System.out.println("  Unsolved conjecture: every positive start is believed to reach 1.");
        System.out.println("  This tool measures stopping time, peak value, and more in 64-bit long.");
        printLine(RULE_HEAVY);
        System.out.println();
    }

    public static void printMainMenu() {
        System.out.println("  MAIN MENU");
        printLine(RULE_LIGHT);
        System.out.println("  1  Analyze one starting value (steps, peak, optional full path)");
        System.out.println("  2  Range scan - table of steps and peaks for many starts");
        System.out.println("  3  Compare two starting values (side-by-side + parallel divergence)");
        System.out.println("  4  Short primer on the Collatz problem");
        System.out.println("  5  Exit");
        printLine(RULE_LIGHT);
    }

    public static void printPrimer() {
        printLine(RULE_HEAVY);
        System.out.println("  PRIMER");
        printLine(RULE_LIGHT);
        System.out.println("  Pick a positive integer n. Repeat:");
        System.out.println("    - If n is even, replace n with n / 2.");
        System.out.println("    - If n is odd,  replace n with 3n + 1.");
        System.out.println("  The Collatz conjecture claims you always eventually reach 1.");
        System.out.println("  After 1, the cycle 4 -> 2 -> 1 repeats.");
        printLine(RULE_LIGHT);
        System.out.println("  Stopping time = how many steps until you first hit 1.");
        System.out.println("  Peak = largest value on the way (can be much larger than the start).");
        System.out.println("  Safe starts for this program: 1 .. " + formatLong(CollatzMath.MAX_SAFE_START));
        printLine(RULE_HEAVY);
        System.out.println();
    }

    public static void printSequenceAnalysis(SequenceAnalysis analysis) {
        printLine(RULE_HEAVY);
        System.out.println("  RESULT - start " + formatLong(analysis.startingValue()));
        printLine(RULE_LIGHT);
        System.out.println("  Total steps (stopping time):  " + analysis.totalSteps());
        System.out.println("  Peak value reached:           " + formatLong(analysis.peakValue()));
        List<Long> traj = analysis.trajectory();
        if (traj.isEmpty()) {
            printLine(RULE_HEAVY);
            System.out.println();
            return;
        }
        System.out.println();
        System.out.println("  Trajectory (values until 1):");
        printLine(RULE_LIGHT);
        printNumberedTrajectory(traj);
        if (analysis.trajectoryTruncated()) {
            System.out.println();
            System.out.println("  (Trajectory truncated after " + traj.size() + " stored terms; "
                    + "stats above are still complete.)");
        }
        printLine(RULE_HEAVY);
        System.out.println();
    }

    private static void printNumberedTrajectory(List<Long> traj) {
        final int perLine = 6;
        StringBuilder line = new StringBuilder();
        int count = 0;
        for (int i = 0; i < traj.size(); i++) {
            if (count == 0) {
                line.append(String.format("  %4d: ", i + 1));
            }
            line.append(formatLong(traj.get(i)));
            count++;
            if (count < perLine && i < traj.size() - 1) {
                line.append("  ");
            }
            if (count == perLine || i == traj.size() - 1) {
                System.out.println(line);
                line.setLength(0);
                count = 0;
            }
        }
    }

    public static void printRangeTable(List<RangeRow> rows) {
        if (rows.isEmpty()) {
            return;
        }
        printLine(RULE_HEAVY);
        System.out.println("  RANGE SCAN");
        printLine(RULE_LIGHT);
        System.out.printf("  %-14s  %12s  %20s%n", "Start", "Steps", "Peak");
        printLine(RULE_LIGHT);
        for (RangeRow row : rows) {
            System.out.printf("  %-14s  %12d  %20s%n",
                    formatLong(row.startValue()),
                    row.totalSteps(),
                    formatLong(row.peakValue()));
        }
        printLine(RULE_HEAVY);
        System.out.println();
    }

    public static void printComparison(ComparisonSummary summary) {
        SequenceAnalysis a = summary.first();
        SequenceAnalysis b = summary.second();
        printLine(RULE_HEAVY);
        System.out.println("  COMPARISON");
        printLine(RULE_LIGHT);
        System.out.printf("  %-18s  %14s  %14s%n", "", "First", "Second");
        System.out.printf("  %-18s  %14s  %14s%n", "Start",
                formatLong(a.startingValue()),
                formatLong(b.startingValue()));
        System.out.printf("  %-18s  %14d  %14d%n", "Steps to 1", a.totalSteps(), b.totalSteps());
        System.out.printf("  %-18s  %14s  %14s%n", "Peak",
                formatLong(a.peakValue()),
                formatLong(b.peakValue()));
        printLine(RULE_LIGHT);
        int div = summary.indexOfFirstDivergence();
        if (div < 0) {
            System.out.println("  Parallel walk: values match at every step until 1 (same start).");
        } else {
            System.out.println("  Parallel walk: first differing value at step index " + div
                    + " (0 = starting pair).");
        }
        printLine(RULE_HEAVY);
        System.out.println();
    }

    public static void printError(String message) {
        System.out.println();
        System.out.println("  ! " + message);
        System.out.println();
    }

    public static void printGoodbye() {
        System.out.println();
        printLine(RULE_LIGHT);
        System.out.println("  Goodbye - keep exploring.");
        printLine(RULE_LIGHT);
        System.out.println();
    }

    private static void printLine(char c) {
        System.out.println(String.valueOf(c).repeat(LINE_WIDTH));
    }

    private static String formatLong(long value) {
        return String.format("%,d", value);
    }
}
