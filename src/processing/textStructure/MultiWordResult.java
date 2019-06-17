package processing.textStructure;

import java.io.IOException;

/**
 * This class defines a query result for multiple non-consecutive words.
 */
public class MultiWordResult extends WordResult implements Comparable<MultiWordResult> {
	private long[] wordPositions;
	private int confidence;



	private MultiWordResult(Block blk, String[] query, long idx) {
		super(blk, query, idx);
	}

	/**
	 * Constructor
	 * @param query The list of query words
	 * @param block The block where this result came from
	 * @param locs  The indices of the words in the block
	 */
	public MultiWordResult(String[] query, Block block, long[] locs) {
	
	}

	/**
	 * Calculate the confidence level of a result, defined by the sum of word distances.
	 * @param locs  The locations of the query words in the text
	 * @return  The sum of distances
	 */
	private int calcConfidence(long[] locs) {
	
	}

	/**
	 * Comparator for multy-word results
	 * @param o The other result to compare against
	 * @return  int representing comparison result, according to the comparable interface.
	 */
	@Override
	public int compareTo(MultiWordResult o) {

	
	}

	/**
	 * Extract a string that contains all words in the multy-word-result
	 * This should be a sentance starting at the word with the minimal location (index) and ending
	 * at the first line-break after the last word
	 * @return  A piece of text containing all query words
	 */
	@Override
	public String resultToString() throws IOException {
	
	}


}
