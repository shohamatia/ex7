package dataStructures.dictionary;

import dataStructures.Aindexer;
import processing.parsingRules.IparsingRule;
import processing.searchStrategies.DictionarySearch;
import processing.textStructure.Block;
import processing.textStructure.Corpus;
import processing.textStructure.Entry;
import processing.textStructure.Word;
import utils.Stemmer;
import utils.Stopwords;
import utils.WrongMD5ChecksumException;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of the abstract Aindexer class, backed by a simple hashmap to store words and their
 * locations within the files.
 */
public class DictionaryIndexer extends Aindexer<DictionarySearch> {
    private static final String regexForWord = "[a-zA-Z0-9]+";
    private static final Stemmer STEMMER = new Stemmer();
    public static final IndexTypes TYPE_NAME = IndexTypes.DICT;
    private final String fileIndexerPath;
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
            //Check unchanged corpus
            String originCashedChecksum = (String) in.readObject();
            if (!origin.getChecksum().equals(originCashedChecksum))
                throw new WrongMD5ChecksumException();

            //Read Dict
            Object dictObj = in.readObject();
            if (!(dictObj instanceof HashMapWrapper))
                throw new WrongMD5ChecksumException();
            HashMapWrapper dictReader = (HashMapWrapper) dictObj;
            this.dict = dictReader.getMap();

            //Read origin
            Object originObj = in.readObject();
            if (!(originObj instanceof Corpus))
                throw new WrongMD5ChecksumException();
            this.origin = (Corpus) originObj;

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            throw new FileNotFoundException();
        }
    }

    @Override
    protected void writeIndexFile() {
        try (FileOutputStream file = new FileOutputStream(fileIndexerPath);
             ObjectOutputStream out = new ObjectOutputStream(file)) {
            String originChecksum = origin.getChecksum();
            out.writeObject(originChecksum);
            out.writeObject(new HashMapWrapper(dict));
            out.writeObject(origin);
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
                    if(Stopwords.isStemmedStopword(word))
                            continue;
                    word = STEMMER.stem(word.toLowerCase());
                    if(word.isEmpty())
                        continue;
                    int key = word.hashCode();
                    dict.putIfAbsent(key, new LinkedList<>());
                    Word newWord = new Word(block, m.start(), m.end());
                    dict.get(key).add(newWord);
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
