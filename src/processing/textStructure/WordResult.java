package processing.textStructure;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a result containing a single string (single word or multiple words treated as one)
 */
public class WordResult {
    private final static String RESULTS_SEPARATOR =
            String.join("", Collections.nCopies(256, "=")) + "\n";
    private long idxInBlk;
    private Block location;
    protected String[] content;

    /**
     * Simple constructor without index.
     *
     * @param blk  The block where this word was found
     * @param word The word queried, represented as an array of size 1.
     */
    private WordResult(Block blk, String[] word) {
        this.content = word;
        this.location = blk;
    }

    /**
     * Constructor containing index of word in block
     *
     * @param blk  The block where this word was found
     * @param word The word queried, represented as an array of size 1.
     * @param idx  The index within the block where the word was found.
     */
    public WordResult(Block blk, String[] word, long idx) {
        this(blk, word);
        this.idxInBlk = idx;
    }

    /**
     * Getter for the result's block
     *
     * @return The block where this word was found.
     */
    public Block getBlock() {
        return this.location;
    }

    /**
     * Getter for the queried word for this result
     *
     * @return The query word that generated this result
     */
    public String[] getWord() {
        return this.content;
    }

    /**
     * Method for printing the result
     *
     * @return The result representation as defined by the "printing results" requirement in the exercise
     * instructions.
     * @throws IOException
     */
    public String resultToString() throws IOException {
        long end = this.idxInBlk +
                this.location.getStartIndex() +
                String.join(" ", this.content).length();
        return resultToString(end);
    }

    protected String resultToString(long endLoc) throws IOException {
        List<String> metaData = this.location.getMetadata();
        StringBuilder representation = new StringBuilder(RESULTS_SEPARATOR);
        long fullStartLoc = this.idxInBlk + this.location.getStartIndex();
        RandomAccessFile raf = this.location.getRAF();
        raf.seek(fullStartLoc);
        while (raf.getFilePointer() < endLoc)
            representation.append(raf.readLine());
        for (int i = 0; i < metaData.size(); i++) {
            if (i != metaData.size() - 1) {
                representation.append(metaData.get(i)).append("\n");
            } else {
                representation.append(metaData.get(i));
            }
            //TODO probably need a return at the ed of last line as well
        }
        return representation.toString();
    }
}
