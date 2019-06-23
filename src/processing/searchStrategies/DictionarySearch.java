package processing.searchStrategies;

import processing.textStructure.Word;
import processing.textStructure.WordResult;

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
        int hash = query.hashCode();
        if (!dict.containsKey(hash))
            return null;
        List<Word> words = dict.get(hash);
        List<WordResult> wordResults = new LinkedList<>();
        for (Word word : words) {
            if (!word.toString().equals(query))
                continue;
            WordResult wordResult = new WordResult(
                    word.getSrcBlk(),
                    new String[]{query},
                    word.getEntryIndex() - word.getSrcBlk().getStartIndex());
            wordResults.add(wordResult);
        }
        return wordResults;
    }
}
