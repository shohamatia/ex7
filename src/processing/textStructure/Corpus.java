package processing.textStructure;
import com.sun.deploy.nativesandbox.NativeSandboxBroker;
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

    public Corpus(String path, String parserName) throws IOException {
		name = parserName;
		parsingFactory();
		entryList = new LinkedList<>();
		paths = new ArrayList<>();

		corpusPath = path;
		File source = new File(getPath());
		File[] list = source.listFiles();

		if(list != null){
			recursiveFile(list);
		}
		else {
			throw new IOException();
		}
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
			if (check.isDirectory()) {
				File[] list = check.listFiles();
				if (list != null) {
					recursiveFile(list);
				}
			}
			else {
				Entry entry = new Entry(check.getPath(), parsingRule);
				entryList.add(entry);
				paths.add(check.getPath());
			}
		}
	}

	/**
	 * This method populates the Block lists for each Entry in the corpus.
	 */
	public void populate(){
    	///TODO
    }
    

	/**
	 * The path to the corpus folder
	 * @return A String representation of the absolute path to the corpus folder
	 */
	private String getPath() {
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
	public void updateRAFs() throws ClassNotFoundException {
		for(Entry entry: entryList) {
            FileInputStream file = null;
            File file_old = null;
            try {
                file = new FileInputStream(entry.getPath());
                ObjectInputStream in = new ObjectInputStream(file);
                file_old = (File) in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	/** parsing factory, creates an instance of new parsing rule */
	private void parsingFactory(){
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
