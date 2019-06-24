package dataStructures.dictionary;

import processing.textStructure.Word;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

class HashMapWrapper implements Serializable {

    transient HashMap<Integer, List<Word>> map;

    HashMapWrapper(HashMap<Integer, List<Word>> map) {
        this.map = map;
    }

    HashMap<Integer, List<Word>> getMap() {
        return map;
    }
}
