package processing.textStructure;

import processing.parsingRules.IparsingRule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a single file within a Corpus
 */
public class Entry implements Iterable<Block>, Serializable {
	public static final long serialVersionUID = 1L;
	private ArrayList<Block> blocksList;
	private String path;
	private File file;
	private IparsingRule parseRule;
	private String name;

	String getPath(){
	    return this.path;
    }


	public Entry(String filePath, IparsingRule parseRule) {
	    this.path = filePath;
		this.file = new File(this.path);
		this.parseRule = parseRule;
		this.name = this.file.getName();
	}

	void populate(){
		try {
			RandomAccessFile inputFile = new RandomAccessFile(file, "rw");
			blocksList = new ArrayList<>(parseRule.parseFile(inputFile));
			for (Block block: blocksList){
				block.setEntryName(this.name);
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			///todo
		}
	}

	/**
	 * Iterate over Block objects in the Entry
	 * @return a block iterator
	 */
	public Iterator<Block> iterator() {
		return blocksList.iterator();
	}
}
