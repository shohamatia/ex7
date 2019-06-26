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
    private File source;

    public Corpus(String path, String parserName) throws IllegalArgumentException {
        name = parserName;
        this.createParserRule();
        entryList = new LinkedList<>();

        corpusPath = path;
        this.source = new File(getPath());

        File[] list = source.listFiles();

        if (list == null) {
            throw new IllegalArgumentException("Error: received non directory source file");
        }

        recursiveFile(list);
    }

    /**
     * this method goes over all the files in the directory recursively
     *
     * @param source source to check if directory
     */
    private void recursiveFile(File[] source) {
        for (File check : source) {
            File[] list = check.listFiles();
            if (list == null) {
                Entry entry = new Entry(check.getPath(), parsingRule);
                entryList.add(entry);
                continue;
            }

            recursiveFile(list);
        }
    }

    /**
     * This method populates the Block lists for each Entry in the corpus.
     */
    public void populate() throws FileNotFoundException{
        for (Entry entry : this)
            entry.populate();
    }


    /**
     * The path to the corpus folder
     *
     * @return A String representation of the absolute path to the corpus folder
     */
    public String getPath() {
        return corpusPath;
    }

    /**
     * Iterate over Entry objects in the Corpus
     *
     * @return An entry iterator
     */
    public Iterator<Entry> iterator() {
        return this.entryList.iterator();
    }

    /**
     * Return the checksum of the entire corpus. This is an MD5 checksum which
     * represents all the files in the corpus.
     *
     * @return A string representing the checksum of the corpus.
     */
    public String getChecksum() {
        StringBuilder str = new StringBuilder();
        for (Entry entry : this) str.append(getFileChecksum(entry.getPath()));
        return str.toString();
    }

    /**
     * return the checksum of a specific file
     */
    private StringBuilder getFileChecksum(String path) {
        StringBuilder stringBuilder = new StringBuilder();
        final Scanner s = new Scanner(path);
        while (s.hasNextLine()) {
            stringBuilder.append(MD5.getMd5(s.nextLine()));
        }
        return stringBuilder;
    }

    /**
     * Return the parsing rule used for this corpus
     *
     * @return the parsing rule used for this corpus
     */
    public IparsingRule getParsingRule() {
        return this.parsingRule;
    }

    /**
     * Update the RandomAccessFile objects for the Entries in the corpus,
     * if it was loaded from cache.
     */
    public void updateRAFs() throws FileNotFoundException{
        for (Entry entry : entryList) {
            String path = entry.getPath();
            for (Block block : entry) {
                block.setRAF(path);
            }
        }
    }

    /**
     * parsing factory, creates an instance of new parsing rule
     */
    private void createParserRule() {
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
