package dataStructures.dictionary;

import dataStructures.Aindexer;
import processing.parsingRules.IparsingRule;
import processing.searchStrategies.DictionarySearch;
import processing.textStructure.Corpus;
import processing.textStructure.Word;
import utils.Stemmer;
import utils.WrongMD5ChecksumException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

/**
 * An implementation of the abstract Aindexer class, backed by a simple hashmap to store words and their
 * locations within the files.
 */
public class DictionaryIndexer extends Aindexer<DictionarySearch> {

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
