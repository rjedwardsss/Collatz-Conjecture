package collatz.model;

/**
 * One row in a range scan: stopping time and peak for a single starting value.
 */
public record RangeRow(long startValue, int totalSteps, long peakValue) {
}
