package processing.textStructure;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * Wrapper class for a single word containing relevant attributes for distance calculation and string
 * extraction from the containing block or file.
 */
public class Word implements Serializable {
    public static final long serialVersionUID = 1L;

    /**
     * A reference for the contaning Block object.
     */
    private final Block srcBlk;
    /**
     * The offset of the word within the block
     */
    private final long srcBlkOffset;
    /**
     * Length of the word
     */
    private final int length;
    /**
     * Hash of the word - for quick dictionary querying without unnecessary extraction and conversion.
     */
    private final int wordHash;

    /**
     * The constructor.
     *
     * @param source   The Block where this word resides.
     * @param startIdx The offset within the block where the word starts.
     * @param endIdx   The offset within the block where the word ends.
     */
    public Word(Block source, long startIdx, long endIdx) throws IOException {
        this.srcBlk = source;
        this.srcBlkOffset = startIdx;
        this.length = (int) (endIdx - startIdx);
        this.wordHash = extractWord().hashCode();
    }

    /**
     * Simple getter
     *
     * @return The source block
     */
    public Block getSrcBlk() {
        return this.srcBlk;
    }

    /**
     * Get the actual string of the word from within the block.
     *
     * @return The word in String format.
     */
    protected String extractWord() throws IOException {
        RandomAccessFile raf = this.srcBlk.getRAF();
        String word;
        byte[] wordBytesArray = new byte[this.length];
        raf.seek(getEntryIndex());
        for (int i = 0; i < this.length; i++) {
            wordBytesArray[i] = raf.readByte();
        }
        word = new String(wordBytesArray);
        return word;
    }

    /**
     * Get the hashCode of the word
     *
     * @return The wordHash.
     */
    public int getHash() {
        return this.wordHash;
    }

    /**
     * The source block offset within the file + the offset of the word within the block = offset within an entry!
     *
     * @return offset within the entire FILE where the word resides.
     */
    public long getEntryIndex() {
        return this.srcBlk.getStartIndex() + this.srcBlkOffset;
    }

    @Override
    public String toString() {
        try {
            return extractWord();
        } catch (IOException e) {
            System.out.println("Failed to read word to string");
            return "";
        }
    }
}
