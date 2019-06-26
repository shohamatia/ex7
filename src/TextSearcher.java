import dataStructures.Aindexer;
import dataStructures.dictionary.DictionaryIndexer;
import dataStructures.naive.NaiveIndexer;
import dataStructures.naive.NaiveIndexerRK;
import processing.textStructure.Corpus;
import processing.textStructure.WordResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * The test program - A text searching module that indexes and queries large corpses for strings or word
 * groups.
 */
public class TextSearcher {
    private final static String WRONG_NUM_OF_ARGS_ERROR = "This function should only receive a single " +
            "argument.";
    private final static String INVALID_INPUT_ARGUMENTS_FILE_ERROR = "The input was an invalid file.";
    private final static String QUERY_RESULTS = "The top 10 results for query '%s' are:";


    /**
     * Main method. Reads and parses a command file and if a query exists, prints the results.
     *
     * @param args - program arguments.
     */
    public static void main(String[] args) {
        Configuration conf;
        Corpus origin;

        try {
            List<String> lines = checkLegitFile(args);
            conf = new Configuration(lines);

            origin = new Corpus(conf.getCorpusPathAddress(), conf.getParseRuleString());
        } catch (IOException e) {
            System.out.println(INVALID_INPUT_ARGUMENTS_FILE_ERROR);
            System.out.println(e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        Aindexer<?> indexer = getIndexer(conf.getIndexType(), origin);
        try {
            indexer.index();
        } catch (IOException e) {
            System.out.println("Error writing to index.");
        }


        if (!conf.hasQuery()) {
            return;
        }

        String query = conf.getQuery();
        List<? extends WordResult> results = indexer.asSearchInterface().search(query);
        System.out.println(String.format(QUERY_RESULTS, query));
        if (results == null || results.size() == 0) {
            System.out.println("empty or null results");
            return;
        }
        for (int i = 0; i < Math.min(results.size(), 10); i++) {
            WordResult result = results.get(i);
            try {
                System.out.println(result.resultToString());
            } catch (IOException e) {
                System.out.println("IO problem while reading search results");
            }

        }
    }

    private static List<String> checkLegitFile(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IOException(WRONG_NUM_OF_ARGS_ERROR);
        }
        String path_name = args[0];
        Path path = Paths.get(path_name);
        return Files.readAllLines(path);
    }


    static Aindexer<?> getIndexer(Aindexer.IndexTypes indexType, Corpus origin) {
        switch (indexType) {
            case DICT:
                return new DictionaryIndexer(origin);
            case NAIVE_RK:
                return new NaiveIndexerRK(origin);
            default:
                return new NaiveIndexer(origin);
        }
    }
}