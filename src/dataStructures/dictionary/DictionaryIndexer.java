package dataStructures.dictionary;

import dataStructures.Aindexer;
import processing.parsingRules.IparsingRule;
import processing.searchStrategies.DictionarySearch;
import processing.textStructure.Block;
import processing.textStructure.Corpus;
import processing.textStructure.Entry;
import processing.textStructure.Word;
import utils.Stemmer;
import utils.WrongMD5ChecksumException;

import java.io.FileNotFoundException;
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
    private static final String regexForWord = "[^\\s]*";
    private static final Stemmer STEMMER = new Stemmer();
    public static final IndexTypes TYPE_NAME = IndexTypes.DICT;
    private Corpus origin;
    private HashMap<Integer, List<Word>> dict;

    /**
     * Basic constructor, sets origin Corpus and initializes backing hashmap
     *
     * @param origin the Corpus to be indexed by this DS.
     */
    public DictionaryIndexer(Corpus origin) {
        super(origin);
        this.origin = origin;
        this.dict = new HashMap<>();
    }


    @Override
    protected void readIndexedFile() throws WrongMD5ChecksumException, FileNotFoundException {
        throw new FileNotFoundException();
    }

    @Override
    protected void writeIndexFile() {


    }

    @Override
    protected void indexCorpus() {
        final Pattern p = Pattern.compile(regexForWord);
        final Matcher m = p.matcher("");
        for (Entry entry:origin){
            for (Block block: entry){
                String blockString = block.toString();
                m.reset(blockString);
                while (m.find()){
                    int key = m.group().hashCode();
                    if (!dict.containsKey(key))
                        dict.put(m.group().hashCode(),new LinkedList<>());
                    dict.get(key).add(new Word(block, m.start(),m.end()));
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
