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
        this.fileIndexerPath = TYPE_NAME.toString() + "_" + origin.getParsingRule().toString() + "_" + origin
                .getPath();

    }

    private byte[] getObjectAsBytes(Object obj){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] bytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            bytes = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        return bytes;
    }

    private String[] getObjectsChecksum(){
        String[] stringArray = new String[2];
        byte[] curOriginAsBytes = getObjectAsBytes(this.origin);
        stringArray[0] = MD5.getMd5(curOriginAsBytes);
        byte[] curDictAsBytes = getObjectAsBytes(this.dict);
        stringArray[1] = MD5.getMd5(curDictAsBytes);
        return stringArray;
    }

    @Override
    protected void readIndexedFile() throws WrongMD5ChecksumException, FileNotFoundException {
        String originChecksum = getObjectsChecksum()[0];
        String dictChecksum = getObjectsChecksum()[1];
        FileInputStream file = new FileInputStream(fileIndexerPath);
        try (ObjectInputStream in = new ObjectInputStream(file)){
            Object originByteArray = in.readObject();
            byte[] originAsBytes = getObjectAsBytes(originByteArray);
            if (MD5.getMd5(originChecksum).equals(MD5.getMd5(originAsBytes))) {
                Object originObj = in.readObject();
                if (originObj instanceof Corpus) {
                    this.origin = (Corpus) originObj;
                }else throw new WrongMD5ChecksumException();
            }else throw new WrongMD5ChecksumException();
            Object dictByteArray = in.readObject();
            byte[] dictAsBytes = getObjectAsBytes(dictByteArray);
            if (MD5.getMd5(dictAsBytes).equals(MD5.getMd5(dictChecksum))){
                Object dictObj = in.readObject();
                if (dictObj instanceof HashMapWrapper) {
                    HashMapWrapper dictReader = (HashMapWrapper) dictObj;
                    this.dict = dictReader.getMap();
                }else throw new WrongMD5ChecksumException();
            }else throw new WrongMD5ChecksumException();
            file.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void writeIndexFile() {
        String originChecksum = getObjectsChecksum()[0];
        String dictChecksum = getObjectsChecksum()[1];
        HashMapWrapper dictToWrite = new HashMapWrapper(dict);
        try {
            FileOutputStream file = new FileOutputStream(fileIndexerPath);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(originChecksum);
            out.writeObject(origin);
            out.writeObject(dictChecksum);
            out.writeObject(dictToWrite);
            out.close();
            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
                    String word =m.group();
                    word = STEMMER.stem(word);
                    int key = word.hashCode();
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
