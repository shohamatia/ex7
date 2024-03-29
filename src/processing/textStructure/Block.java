package processing.textStructure;


import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents an arbitrary block of text within a file.
 */
public class Block implements Serializable {
    public static final long serialVersionUID = 1L;
    private String entryName;
    private long endIdx;
    private transient RandomAccessFile inputFile;
    private long startIdx;
    private LinkedList<String> metaData = new LinkedList<>();

    /**
     * Constructor
     *
     * @param inputFile the RAF object backing this block
     * @param startIdx  start index of the block within the file
     * @param endIdx    end index of the block within the file
     */
    public Block(RandomAccessFile inputFile, long startIdx, long endIdx)
            throws IllegalArgumentException, IOException {
        this.inputFile = inputFile;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        if (startIdx < 0 || startIdx >= endIdx || endIdx > inputFile.length())
            throw new IllegalArgumentException("Tried to create block with illegal indices.");
    }

    /**
     * The filename from which this block was extracted
     *
     * @return filename
     */
    public String getEntryName() throws NoSuchFieldException {
        if (entryName == null)
            throw new NoSuchFieldException("called getEntryName on a block without setting it");
        return entryName;
    }

    void setEntryName(String entryName) {
        this.entryName = entryName;
        metaData.add("Taken out of entry: \"" + entryName + "\"");
    }


///////// getters //////////

    /**
     * @return start index
     */
    public long getStartIndex() {
        return startIdx;
    }

    /**
     * @return end index
     */
    public long getEndIndex() {
        return endIdx;
    }

    /**
     * Convert an abstract block into a string
     *
     * @return string representation of the block (the entire text of the block from start to end indices)
     */
    @Override
    public String toString() {
        try {
            List<Byte> bytes = new LinkedList<>();
            this.inputFile.seek(this.startIdx);
            while (this.inputFile.getFilePointer() < this.endIdx) {
                bytes.add(this.inputFile.readByte());
            }
            byte[] byteArray = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                byteArray[i] = bytes.get(i);
            }
            return new String(byteArray);
        } catch (IOException e) {
            System.out.println("ERROR: couldn't read block file to string");
            return " ";
        }
    }

    /**
     * Adds metadata to the block
     *
     * @param metaData A list containing metadata entries related to this block
     */
    public void setMetadata(List<String> metaData) {
        this.metaData = new LinkedList<>(metaData);
    }

    /**
     * @return the RAF object for this block
     */
    public RandomAccessFile getRAF() {
        return inputFile;
    }

    void setRAF(String path) throws FileNotFoundException {
        this.inputFile = new RandomAccessFile(path, "rw");
    }

    /**
     * Get the metadata of the block, if applicable for the parsing rule used
     *
     * @return String of all metadata.
     */
    public List<String> getMetadata() {
        return this.metaData;
    }
}
