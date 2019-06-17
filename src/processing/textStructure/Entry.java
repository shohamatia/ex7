package processing.textStructure;

import processing.parsingRules.IparsingRule;

import java.io.Serializable;
import java.util.Iterator;

/**
 * This class represents a single file within a Corpus
 */
public class Entry implements Iterable<Block>, Serializable {
	public static final long serialVersionUID = 1L;
    

    public Entry(String filePath, IparsingRule parseRule) {
    
    }


	/**
	 * Iterate over Block objects in the Entry
	 * @return a block iterator
	 */
	@Override
    public Iterator<Block> iterator() {
    
    }

	
}
