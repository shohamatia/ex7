package dataStructures.dictionary;

import dataStructures.Aindexer;
import processing.parsingRules.IparsingRule;
import processing.searchStrategies.DictionarySearch;
import processing.textStructure.Block;
import processing.textStructure.Corpus;
import processing.textStructure.Entry;
import processing.textStructure.Word;
import utils.MD5;
import utils.Stemmer;
import utils.WrongMD5ChecksumException;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of the abstract Aindexer class, backed by a simple hashmap to store words and their
 * locations within the files.
 */
public class DictionaryIndexer extends Aindexer<DictionarySearch> {
    private static final String regexForWord = "[^\\s]+";
    private static final Stemmer STEMMER = new Stemmer();
    public static final IndexTypes TYPE_NAME = IndexTypes.DICT;
    private final String fileIndexerPath;
    private transient Corpus origin;
    private transient HashMap<Integer, List<Word>> dict;

    /**
     * Basic constructor, sets origin Corpus and initializes backing hashmap
     *
     * @param origin the Corpus to be indexed by this DS.
     */
    public DictionaryIndexer(Corpus origin) {
        super(origin);
        this.origin = origin;
        this.dict = new HashMap<>();
        this.fileIndexerPath = TYPE_NAME.toString() + "_" + origin.getParsingRule().getClass().getSimpleName()
                + "_" + Paths.get(origin.getPath()).getFileName();

    }

    @Override
    protected void readIndexedFile() throws WrongMD5ChecksumException, FileNotFoundException {
        try (FileInputStream file = new FileInputStream(fileIndexerPath);
             ObjectInputStream in = new ObjectInputStream(file)) {
            String originCashedChecksum = (String) in.readObject();
            if (!origin.getChecksum().equals(originCashedChecksum))
                throw new WrongMD5ChecksumException();
            Object dictObj = in.readObject();
            if (!(dictObj instanceof HashMapWrapper) )
                throw new WrongMD5ChecksumException();
            HashMapWrapper dictReader = (HashMapWrapper) dictObj;
            this.dict = dictReader.getMap();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void writeIndexFile() {
        try (FileOutputStream file = new FileOutputStream(fileIndexerPath);
             ObjectOutputStream out = new ObjectOutputStream(file)) {
            String originChecksum = origin.getChecksum();
            out.writeObject(originChecksum);
            out.writeObject(dict);
            //out.writeObject(origin);
        } catch (IOException e) {
            System.err.println("writeIndexFile error:" + e.getMessage());
        }
    }

    @Override
    protected void indexCorpus() {
        final Pattern p = Pattern.compile(regexForWord);
        final Matcher m = p.matcher("");
        for (Entry entry : origin) {
            for (Block block : entry) {
                String blockString = block.toString();
                m.reset(blockString);
                while (m.find()) {
                    String word = m.group();
                    word = STEMMER.stem(word);
                    int key = word.hashCode();
                    if (!dict.containsKey(key))
                        dict.put(key, new LinkedList<>());
                    dict.get(key).add(new Word(block, m.start(), m.end()));
                }
            }
        }
    }


    @Override
    public IparsingRule getParseRule() {
        return this.origin.getParsingRule();
    }


    @Override
    public DictionarySearch asSearchInterface() {
        return new DictionarySearch(this.dict);
    }


}
