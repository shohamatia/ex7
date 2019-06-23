package processing.textStructure;
import processing.parsingRules.IparsingRule;
import processing.parsingRules.STmovieParsingRule;
import processing.parsingRules.STtvSeriesParsingRule;
import processing.parsingRules.SimpleParsingRule;
import utils.MD5;

import java.io.*;
import java.util.*;

/**
 * This class represents a body of works - anywhere between one and thousands of documents
 * sharing the same structure and that can be parsed by the same parsing rule.
 */
public class Corpus implements Iterable<Entry>, Serializable {
	public static final long serialVersionUID = 1L;
    private List<Entry> entryList;
    private IparsingRule parsingRule;
    private String name;
    private String corpusPath;
    private List<String> paths;
	private File source;

    public Corpus(String path, String parserName) {
		name = parserName;
		this.createParserRule();
		entryList = new LinkedList<>();
		paths = new ArrayList<>();

		corpusPath = path;
		this.source = new File(getPath());
    }

        /*
        check if the path is a folder or file.
        if file - single entry corpus.
        otherwise, recursively scan the directory for all subdirectories and files.
        each entry in a corpus should hold the folder from which the file came.
         */

	/** this method goes over all the files in the directory recursively
	 * @param source source to check if directory
	 */
	private void recursiveFile(File[] source){
		for(File check: source) {
			File[] list = check.listFiles();
			if (list != null) {
				recursiveFile(list);
			} else {
				Entry entry = new Entry(check.getPath(), parsingRule);
				entryList.add(entry);
				paths.add(check.getPath());
			}
		}
	}

	/**
	 * This method populates the Block lists for each Entry in the corpus.
	 */
	public void populate() {
		File[] list = source.listFiles();

		System.out.println("Starting to populate");

		if(list != null){
			recursiveFile(list);
		}
		else {
			System.out.println("received non directory source file");
		}

		System.out.println("finished populating");
	}
    

	/**
	 * The path to the corpus folder
	 * @return A String representation of the absolute path to the corpus folder
	 */
	public String getPath() {
		return corpusPath;
    }

	/**
	 * Iterate over Entry objects in the Corpus
	 * @return An entry iterator
	 */
    public Iterator<Entry> iterator() {
		return this.entryList.iterator();
    }

	/**
	 * Return the checksum of the entire corpus. This is an MD5 checksum which
	 * represents all the files in the corpus.
	 * @return A string representing the checksum of the corpus.
	 * @throws IOException if any file is invalid.
	 */
	public String getChecksum() throws IOException {
		StringBuilder str = new StringBuilder();
		for(String path: paths){
			if(path.isEmpty()){
				throw new IOException();
			}
			else{
				str.append(getFileChecksum(path));
			}
		}
		return str.toString();
    }

    /**
     * return the checksum of a specific file
     */
    private StringBuilder getFileChecksum(String path){
		StringBuilder stringBuilder = new StringBuilder();
		final Scanner s = new Scanner(path);
		while(s.hasNextLine()) {
			stringBuilder.append(MD5.getMd5(s.nextLine()));
		}
		return stringBuilder;
	}

	/**
	 * Return the parsing rule used for this corpus
	 * @return the parsing rule used for this corpus
	 */
	public IparsingRule getParsingRule() {
		return this.parsingRule;
    }

	/**
	 * Update the RandomAccessFile objects for the Entries in the corpus,
	 * if it was loaded from cache.
	 */
	public void updateRAFs() {
		for(Entry entry: entryList) {
		    String path = entry.getPath();
		    for (Block block : entry){
		        block.setRAF(path);
            }
        }
	}

	/** parsing factory, creates an instance of new parsing rule */
	private void createParserRule(){
		switch (name) {
			case "SIMPLE":
				parsingRule = new SimpleParsingRule();
				break;
			case "ST_MOVIE":
				parsingRule = new STmovieParsingRule();
				break;
			case "ST_TV":
				parsingRule = new STtvSeriesParsingRule();
				break;
		}
	}
}
