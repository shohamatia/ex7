package processing.searchStrategies;

import processing.textStructure.Block;
import processing.textStructure.MultiWordResult;
import processing.textStructure.Word;
import processing.textStructure.WordResult;
import utils.Stemmer;
import utils.Stopwords;

import java.util.*;

public class DictionarySearch implements IsearchStrategy {
    private HashMap<Integer, List<Word>> dict;
    private static Stemmer stemmer = new Stemmer();


    public DictionarySearch(HashMap<Integer, List<Word>> dict) {
        this.dict = dict;
    }

    @Override
    public List<? extends WordResult> search(String query) {
        String stoppedQuery = Stopwords.removeStemmedStopWords(query);
        String[] queryWords = stoppedQuery.split(" ");


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
        for (Block block : queryWordListsByBlock.keySet()) {
            List<HashMap<String, Word>> fullResults = cartesianProduct(queryWordListsByBlock.get(block));

            for (HashMap<String, Word> result : fullResults) {
                long[] locs = new long[queryWords.length];
                for (int i = 0; i < queryWords.length; i++) {
                    Word word = result.get(queryWords[i]);
                    locs[i] = word.getEntryIndex() - block.getStartIndex();
                }
                MultiWordResult multiWordResult;
                try {
                    multiWordResult = new MultiWordResult(
                            queryWords, block, locs);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                multiWordResults.add(multiWordResult);
            }

        }
        multiWordResults.sort(MultiWordResult::compareTo);
        return multiWordResults;
    }

    /**
     * @param singleWordQuery - single word to find
     * @return list of words matching
     */
    private List<Word> singleWordSearch(String singleWordQuery) {
        singleWordQuery = singleWordQuery.toLowerCase();
        int hash = stemmer.stem(singleWordQuery).hashCode();
        if (!dict.containsKey(hash))
            return null;
        return dict.get(hash);
    }

    /**
     * @param map - the map to do the calculation on
     * @return - the cartesian product of the map.
     */
    private List<HashMap<String, Word>> cartesianProduct(HashMap<String, List<Word>> map) {
        if (map.keySet().size() == 0)
            return new LinkedList<HashMap<String, Word>>() {{
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
