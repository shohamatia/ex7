package processing.searchStrategies;

import processing.textStructure.Block;
import processing.textStructure.Corpus;
import processing.textStructure.Entry;
import processing.textStructure.WordResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements naive searching.
 */
public class NaiveSearch implements IsearchStrategy {
    protected Corpus origin;

    /**
     * constructor
     */
    public NaiveSearch(Corpus origin) {
        this.origin = origin;
    }

    /**
     * @param query The query string to search for.
     * @return list of wordResults objects matching the query.
     */
    public List<WordResult> search(String query) {
        List<WordResult> wordResults = new LinkedList<>();
        for (Entry entry : origin) {
            for (Block block : entry) {
                wordResults.addAll(matchResults(query, block));
            }
        }
        return wordResults;
    }

    /**
     * @param query The query string to search for.
     * @param block the block to search in
     * @return list of wordResults objects matching the query.
     */
    private List<WordResult> matchResults(String query, Block block) {
        List<WordResult> wordResults = new LinkedList<>();
        String blockString = block.toString();
        int i;
        int j;
        for (i = 0; i <= blockString.length() - query.length(); i++) {
            foundMatch:
            {
                for (j = 0; j < query.length(); j++) {
                    if (blockString.charAt(i + j) != query.charAt(j))
                        break foundMatch;
                }
                wordResults.add(new WordResult(block, new String[]{query}, (long) i));
            }
        }
        return wordResults;
    }
}
