package dataStructures.dictionary;

import processing.textStructure.Word;

import java.util.HashMap;
import java.util.List;

public class HashMapWrapper {
    HashMap<Integer, List<Word>> map;

    HashMapWrapper(HashMap<Integer, List<Word>> map) {
        this.map = map;
    }

    public HashMap<Integer, List<Word>> getMap() {
        return map;
    }
}
