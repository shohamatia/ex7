package dataStructures.naive;

import processing.textStructure.Corpus;

public class NaiveIndexerRK extends NaiveIndexer {

    public static final IndexTypes TYPE_NAME = IndexTypes.NAIVE_RK;

    public NaiveIndexerRK(Corpus corpus) {
        super(corpus, true);
    }

}
