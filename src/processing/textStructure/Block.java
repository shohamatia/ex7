package processing.textStructure;

import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.List;

/**
 * This class represents an arbitrary block of text within a file
 */
public class Block implements Serializable {
	public static final long serialVersionUID = 1L;

	private long endIdx;
	transient RandomAccessFile inputFile;
	private long startIdx;

	/**
	 * Constructor
	 * @param inputFile     the RAF object backing this block
	 * @param startIdx      start index of the block within the file
	 * @param endIdx        end index of the block within the file
	 */
	public Block(RandomAccessFile inputFile, long startIdx, long endIdx) {
		this.inputFile = inputFile;
		this.startIdx = startIdx;
		this.endIdx = endIdx;

	}
	
	/**
	 * The filename from which this block was extracted
	 * @return  filename
	 */
	public String getEntryName(){
	
	}



///////// getters //////////
	/**
	 * @return start index
	 */
	public long getStartIndex() {
		return startIdx;
	}
	
	/**
	 * @return  end index
	 */
	public long getEndIndex() {
		return endIdx;
	}
	
	/**
	 * Convert an abstract block into a string
	 * @return  string representation of the block (the entire text of the block from start to end indices)
	 */
	@Override
	public String toString() {

	}
	
	/**
	 * Adds metadata to the block
	 * @param metaData A list containing metadata entries related to this block
	 */
	public void setMetadata(List<String> metaData) {
	}
	
	
	/**
	 * @return the RAF object for this block
	 */
	public RandomAccessFile getRAF() {

	}
	
	/**
	 * Get the metadata of the block, if applicable for the parsing rule used
	 * @return  String of all metadata.
	 */
	public List<String> getMetadata() {

	}
}
