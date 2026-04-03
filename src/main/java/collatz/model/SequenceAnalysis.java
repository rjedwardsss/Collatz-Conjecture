package collatz.model;

import java.util.Collections;
import java.util.List;

/**
 * Outcome of iterating the Collatz map from a positive starting value until 1 is reached.
 *
 * @param startingValue   the seed
 * @param totalSteps      number of applications of the map until the value becomes 1
 * @param peakValue       maximum value observed on the path (including the start)
 * @param trajectory      ordered values from start through 1; empty if not collected
 * @param trajectoryTruncated true if trajectory was capped by {@link collatz.core.CollatzMath.AnalysisOptions}
 */
public record SequenceAnalysis(
        long startingValue,
        int totalSteps,
        long peakValue,
        List<Long> trajectory,
        boolean trajectoryTruncated
) {
    public SequenceAnalysis {
        trajectory = trajectory == null
                ? List.of()
                : List.copyOf(trajectory);
    }

    public static SequenceAnalysis emptyTrajectory(
            long startingValue,
            int totalSteps,
            long peakValue
    ) {
        return new SequenceAnalysis(
                startingValue,
                totalSteps,
                peakValue,
                Collections.emptyList(),
                false
        );
    }
}
