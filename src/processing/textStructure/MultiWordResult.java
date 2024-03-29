package processing.textStructure;

import java.io.IOException;
import java.util.Arrays;

/**
 * This class defines a query result for multiple non-consecutive words.
 */
public class MultiWordResult extends WordResult implements Comparable<MultiWordResult> {
    private long[] wordPositions;
    private int confidence;
    private long min, max;

    /**
     * Constructor
     *
     * @param query The list of query words
     * @param block The block where this result came from
     * @param locs  The indices of the words in the block
     */
    public MultiWordResult(String[] query, Block block, long[] locs) throws IllegalArgumentException {
        super(block, query, Arrays.stream(locs).min().orElse(0));
        this.wordPositions = locs;
        calculateMin();
        calculateMax();
        calcConfidence();
        long totalQueryLength = String.join(" ", query).length();
        if (max - min > 3 * totalQueryLength + 40)
            throw new IllegalArgumentException("This result is too big");
    }

    /**
     * sets the min pos relative to the block
     */
    private void calculateMin() {
        this.min = Arrays.stream(wordPositions).min().orElse(0);
    }

    /**
     * sets the max pos relative to the block
     */
    private void calculateMax() {
        max = 0;
        int maxArrayPosition = 0;
        for (int i = 0; i < wordPositions.length; i++) {
            long potentialMax = Math.max(max, wordPositions[i]);
            if (max != potentialMax) {
                max = potentialMax;
                maxArrayPosition = i;
            }
        }
        max += this.content[maxArrayPosition].length();
    }

    /**
     * Calculate the confidence level of a result, defined by the distance of the min and the max character
     * position in the result.
     */
    private void calcConfidence() {
        confidence = (int) (this.min - this.max);
    }

    /**
     * Comparator for multy-word results
     *
     * @param o The other result to compare against
     * @return int representing comparison result, according to the comparable interface.
     */
    @Override
    public int compareTo(MultiWordResult o) {
        return o.confidence - this.confidence;
    }

    /**
     * Extract a string that contains all words in the multi-word-result
     * This should be a sentence starting at the word with the minimal location (index) and ending
     * at the first line-break after the last word
     *
     * @return A piece of text containing all query words
     */
    @Override
    public String resultToString() throws IOException {
        return super.resultToString(max);
    }


}
