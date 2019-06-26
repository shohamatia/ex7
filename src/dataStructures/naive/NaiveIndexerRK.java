package dataStructures.naive;

import processing.textStructure.Corpus;

/**
 * A "naive" indexer. This approach forgoes actually pre-processing the file, and simply loads the text and
 * searches directly on it using the Rabin-Karp Algorithm.
 */
public class NaiveIndexerRK extends NaiveIndexer {

    public NaiveIndexerRK(Corpus corpus) {
        super(corpus, true);
    }

}
