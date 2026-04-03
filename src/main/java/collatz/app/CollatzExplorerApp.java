package collatz.app;

import collatz.core.CollatzMath;
import collatz.core.CollatzMath.AnalysisOptions;
import collatz.model.ComparisonSummary;
import collatz.model.RangeRow;
import collatz.model.SequenceAnalysis;

import java.util.List;
import java.util.OptionalLong;
import java.util.Scanner;

/**
 * Entry point: menu loop and orchestration. All Collatz mathematics live in {@link collatz.core.CollatzMath}.
 */
public final class CollatzExplorerApp {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new CollatzExplorerApp(scanner).run();
        }
    }

    private final InputReader input;

    private CollatzExplorerApp(Scanner scanner) {
        this.input = new InputReader(scanner);
    }

    private void run() {
        ConsoleView.printBanner();
        boolean exit = false;
        while (!exit) {
            ConsoleView.printMainMenu();
            int choice = input.readMenuChoice(1, 5);
            switch (choice) {
                case 1 -> analyzeSingle();
                case 2 -> analyzeRange();
                case 3 -> compareTwo();
                case 4 -> ConsoleView.printPrimer();
                case 5 -> {
                    ConsoleView.printGoodbye();
                    exit = true;
                }
                default -> { /* invalid: message already printed */ }
            }
        }
    }

    private void analyzeSingle() {
        OptionalLong start = input.readPositiveLong("  Starting value (positive integer): ");
        if (start.isEmpty()) {
            return;
        }
        long n = start.getAsLong();
        if (!CollatzMath.isSafeStart(n)) {
            ConsoleView.printError(
                    "That value is too large for safe 64-bit simulation (max "
                            + String.format("%,d", CollatzMath.MAX_SAFE_START) + ").");
            return;
        }
        boolean showPath = input.readYesNo("  Print full trajectory? [y/N]: ");
        try {
            AnalysisOptions opts = showPath
                    ? AnalysisOptions.withTrajectory(CollatzMath.DEFAULT_MAX_TRAJECTORY_TERMS)
                    : AnalysisOptions.statsOnly();
            SequenceAnalysis analysis = CollatzMath.analyze(n, opts);
            ConsoleView.printSequenceAnalysis(analysis);
        } catch (ArithmeticException | IllegalArgumentException | IllegalStateException e) {
            ConsoleView.printError(e.getMessage());
        }
    }

    private void analyzeRange() {
        OptionalLong fromOpt = input.readPositiveLong("  Range start (inclusive): ");
        if (fromOpt.isEmpty()) {
            return;
        }
        OptionalLong toOpt = input.readPositiveLong("  Range end (inclusive): ");
        if (toOpt.isEmpty()) {
            return;
        }
        long from = fromOpt.getAsLong();
        long to = toOpt.getAsLong();
        try {
            List<RangeRow> rows = CollatzMath.analyzeRange(from, to);
            ConsoleView.printRangeTable(rows);
        } catch (IllegalArgumentException | ArithmeticException e) {
            ConsoleView.printError(e.getMessage());
        }
    }

    private void compareTwo() {
        OptionalLong aOpt = input.readPositiveLong("  First starting value: ");
        if (aOpt.isEmpty()) {
            return;
        }
        OptionalLong bOpt = input.readPositiveLong("  Second starting value: ");
        if (bOpt.isEmpty()) {
            return;
        }
        long a = aOpt.getAsLong();
        long b = bOpt.getAsLong();
        if (!CollatzMath.isSafeStart(a) || !CollatzMath.isSafeStart(b)) {
            ConsoleView.printError(
                    "Each value must be at most "
                            + String.format("%,d", CollatzMath.MAX_SAFE_START)
                            + " for safe 64-bit simulation.");
            return;
        }
        try {
            ComparisonSummary summary = CollatzMath.compare(a, b);
            ConsoleView.printComparison(summary);
        } catch (ArithmeticException | IllegalArgumentException | IllegalStateException e) {
            ConsoleView.printError(e.getMessage());
        }
    }
}
