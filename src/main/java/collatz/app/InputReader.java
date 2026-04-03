package collatz.app;

import java.util.OptionalLong;
import java.util.Scanner;

/**
 * Console input helpers with validation and clear error messages.
 */
public final class InputReader {

    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public OptionalLong readLong(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            ConsoleView.printError("Please enter a number.");
            return OptionalLong.empty();
        }
        try {
            return OptionalLong.of(Long.parseLong(line));
        } catch (NumberFormatException e) {
            ConsoleView.printError("That is not a valid whole number (use digits only, optional leading minus).");
            return OptionalLong.empty();
        }
    }

    public OptionalLong readPositiveLong(String prompt) {
        OptionalLong raw = readLong(prompt);
        if (raw.isEmpty()) {
            return OptionalLong.empty();
        }
        long v = raw.getAsLong();
        if (v < 1) {
            ConsoleView.printError("Value must be at least 1.");
            return OptionalLong.empty();
        }
        return OptionalLong.of(v);
    }

    public int readMenuChoice(int minInclusive, int maxInclusive) {
        OptionalLong raw = readLong("  Enter choice (" + minInclusive + "-" + maxInclusive + "): ");
        if (raw.isEmpty()) {
            return -1;
        }
        long v = raw.getAsLong();
        if (v < minInclusive || v > maxInclusive || v != (int) v) {
            ConsoleView.printError("Choose a whole number between " + minInclusive + " and " + maxInclusive + ".");
            return -1;
        }
        return (int) v;
    }

    public boolean readYesNo(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim().toLowerCase();
        return line.equals("y") || line.equals("yes");
    }
}
