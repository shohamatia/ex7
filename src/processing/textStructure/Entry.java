package processing.textStructure;

import processing.parsingRules.IparsingRule;
import processing.parsingRules.STmovieParsingRule;
import processing.parsingRules.STtvSeriesParsingRule;
import processing.parsingRules.SimpleParsingRule;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a single file within a Corpus
 */
public class Entry implements Iterable<Block>, Serializable {
	public static final long serialVersionUID = 1L;
	transient RandomAccessFile inputFile;
	List<Block> blocksList;
    

    public Entry(String filePath, IparsingRule parseRule) {
		File file = new File(filePath);
		blocksList = parsingFactory(parseRule)
    }


	/**
	 * Iterate over Block objects in the Entry
	 * @return a block iterator
	 */
	@Override
    public Iterator<Block> iterator() {
    
    }
