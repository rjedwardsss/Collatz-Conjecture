package collatz.model;

/**
 * Side-by-side comparison of two Collatz trajectories from different seeds.
 *
 * @param first  analysis for the first starting value
 * @param second analysis for the second starting value
 * @param indexOfFirstDivergence parallel step index (0 = starting pair) where values first differ;
 *                               -1 if the two trajectories match at every index until both hit 1
 */
public record ComparisonSummary(
        SequenceAnalysis first,
        SequenceAnalysis second,
        int indexOfFirstDivergence
) {
}
