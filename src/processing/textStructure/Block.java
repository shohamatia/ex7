package processing.textStructure;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents an arbitrary block of text within a file
 */
public class Block implements Serializable {
	public static final long serialVersionUID = 1L;
	private String entryName;
	private long endIdx;
	private transient RandomAccessFile inputFile;
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
	public String getEntryName()throws Exception{
		if (entryName==null)
			throw new Exception("called getEntryName on a block without setting it");
		return entryName;
	}

	void setEntryName(String entryName){
		this.entryName = entryName;
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
	public String toString() {
		try {
			List<Character> characters = new LinkedList<>();
			this.inputFile.seek(this.startIdx);
			while (this.inputFile.getFilePointer() < this.endIdx){
				characters.add(this.inputFile.readChar());
			}
			return characters.stream().map(String::valueOf).collect(Collectors.joining());
		} catch (IOException e) {
			System.out.println(" ");
		}
		return " ";
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
		return inputFile;
	}
	
	/**
	 * Get the metadata of the block, if applicable for the parsing rule used
	 * @return  String of all metadata.
	 */
	public List<String> getMetadata() {
		return new LinkedList<>();
	}
}
