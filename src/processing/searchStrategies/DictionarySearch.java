package processing.searchStrategies;

import processing.textStructure.Word;
import processing.textStructure.WordResult;
import utils.Stemmer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DictionarySearch implements IsearchStrategy {
    HashMap<Integer, List<Word>> dict;

    public DictionarySearch(HashMap<Integer, List<Word>> dict) {
        this.dict = dict;
    }

    @Override
    public List<? extends WordResult> search(String query) {
        Stemmer stemmer = new Stemmer();
        int hash = stemmer.stem(query).hashCode();
        if (!dict.containsKey(hash))
            return null;
        List<Word> words = dict.get(hash);
        List<WordResult> wordResults = new LinkedList<>();
        for (Word word : words) {
            WordResult wordResult = new WordResult(
                    word.getSrcBlk(),
                    new String[]{query},
                    word.getEntryIndex() - word.getSrcBlk().getStartIndex());
            wordResults.add(wordResult);
        }
        return wordResults;
    }
}
