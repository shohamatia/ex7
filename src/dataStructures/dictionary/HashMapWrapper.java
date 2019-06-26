package dataStructures.dictionary;

import processing.textStructure.Word;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Wrapper class for hashMap<Integer, List<Word>>.
 */
class HashMapWrapper implements Serializable {

    private HashMap<Integer, List<Word>> map;

    HashMapWrapper(HashMap<Integer, List<Word>> map) {
        this.map = map;
    }

    HashMap<Integer, List<Word>> getMap() {
        return map;
    }
}
