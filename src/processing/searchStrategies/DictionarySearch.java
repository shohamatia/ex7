package processing.searchStrategies;

import processing.textStructure.Block;
import processing.textStructure.MultiWordResult;
import processing.textStructure.Word;
import processing.textStructure.WordResult;
import utils.Stemmer;

import java.util.*;

public class DictionarySearch implements IsearchStrategy {
    private HashMap<Integer, List<Word>> dict;
    private static Stemmer stemmer = new Stemmer();


    public DictionarySearch(HashMap<Integer, List<Word>> dict) {
        this.dict = dict;
    }

    @Override
    public List<? extends WordResult> search(String query) {
        String[] queryWords = query.split(" ");

        HashMap<String, List<Word>> queryWordLists = new HashMap<>();
        for (String singleQuery : queryWords)
            queryWordLists.put(singleQuery, singleWordSearch(singleQuery));

        HashMap<Block, HashMap<String, List<Word>>> queryWordListsByBlock = new HashMap<>();
        for (String key : queryWordLists.keySet())
            for (Word word : queryWordLists.get(key)) {
                Block block = word.getSrcBlk();
                HashMap<String, List<Word>> map =
                        queryWordListsByBlock.computeIfAbsent(block, k -> new HashMap<>());
                List<Word> list = map.computeIfAbsent(key, k -> new LinkedList<>());
                list.add(word);
            }

        Set<Block> blocksWithOutFullQuery = new HashSet<>();
        for (Block block : queryWordListsByBlock.keySet())
            if (!queryWordListsByBlock.get(block).keySet().equals(queryWordLists.keySet()))
                blocksWithOutFullQuery.add(block);

        queryWordListsByBlock.keySet().removeAll(blocksWithOutFullQuery);


        List<MultiWordResult> multiWordResults = new LinkedList<>();
        //make cartesian product from lists, iterate over results place into thingy
        for (Block block : queryWordListsByBlock.keySet()) {
            List<HashMap<String, Word>> fullResults = cartesianProduct(queryWordListsByBlock.get(block));
            long[] locs = new long[queryWords.length];
            for (HashMap<String, Word> result : fullResults) {
                for (int i = 0; i < queryWords.length; i++) {
                    locs[i] = result.get(queryWords[i]).getEntryIndex();
                }

            }
            MultiWordResult multiWordResult = new MultiWordResult(
                    queryWords, block, locs);
            multiWordResults.add(multiWordResult);
        }
        multiWordResults.sort(MultiWordResult::compareTo);
        return multiWordResults;
    }

    private List<Word> singleWordSearch(String singleWordQuery) {
        int hash = stemmer.stem(singleWordQuery).hashCode();
        if (!dict.containsKey(hash))
            return null;
        return dict.get(hash);
    }

    private List<HashMap<String, Word>> cartesianProduct(HashMap<String, List<Word>> map) {
        if (map.keySet().size() == 0)
            return new LinkedList<>() {{
                add(new HashMap<>());
            }};

        String key = map.keySet().iterator().next();
        List<Word> words = map.get(key);
        map.remove(key);
        List<HashMap<String, Word>> interim = cartesianProduct(map);
        List<HashMap<String, Word>> fullCartesianProduct = new LinkedList<>();
        for (Word word : words) {
            for (HashMap<String, Word> subMap : interim) {
                HashMap<String, Word> toAdd = new HashMap<>(subMap);
                toAdd.put(key, word);
                fullCartesianProduct.add(toAdd);
            }
        }
        return fullCartesianProduct;

    }
}
