import dataStructures.Aindexer;
import dataStructures.dictionary.DictionaryIndexer;
import dataStructures.naive.NaiveIndexer;
import dataStructures.naive.NaiveIndexerRK;
import processing.textStructure.Corpus;
import processing.textStructure.Word;
import processing.textStructure.WordResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * The main program - A text searching module that indexes and queries large corpuses for strings or word groups
 */
public class TextSearcher {
    private final static String WRONG_NUM_OF_ARGS_ERROR = "This function should only receive a single argument.";
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
            testConfiguration(conf);

            origin = new Corpus(conf.getCorpusPathAddress(), conf.getParseRuleString());
            System.out.println("got origin");
        } catch (IOException e) {
            System.out.println(INVALID_INPUT_ARGUMENTS_FILE_ERROR);
            return;
        }

        System.out.println("gonna index");
        Aindexer indexer = getIndexer(conf.getIndexType(),origin);
        indexer.index();
        System.out.println("indexed");

        if (!conf.hasQuery()) {
            return;
        }

        System.out.println("gonna search");

        String query = conf.getQuery();
        List<? extends WordResult> results = indexer.asSearchInterface().search(query);
        System.out.println(String.format(QUERY_RESULTS, query));
        if(results==null||results.size()==0)
            System.out.println("empty or null results");
        for (int i = 0; i< Math.min(results.size(),10);i++){
            WordResult result = results.get(i);
            try {
                System.out.println(result.resultToString());
            }
            catch (IOException e){
                System.out.println("problem!!!!");
            }

        }
    }

    private static List<String> checkLegitFile(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println(WRONG_NUM_OF_ARGS_ERROR);
            throw new IOException();
        }
        String path_name = args[0];
        Path path = Paths.get(path_name);
        return Files.readAllLines(path);
    }

    static void testConfiguration(Configuration conf) {
        System.out.println("printing indexer type, parser type, path, query if exists");
        System.out.println(conf.getIndexType().toString());
        System.out.println(conf.getParserType().toString());
        System.out.println(conf.getPath().toString());
        try {
            System.out.println(conf.getQuery());
        } catch (Exception e) {
        }
        System.out.println("finished printing configuration inputs");
        System.out.println();
    }

    static Aindexer getIndexer(Aindexer.IndexTypes indexType, Corpus origin) {
        switch (indexType) {
            case DICT:
                return new DictionaryIndexer(origin);
//            case NAIVE:
//                return new NaiveIndexer(origin);
            case NAIVE_RK:
                return new NaiveIndexerRK(origin);
            default:
                return new NaiveIndexer(origin);
        }
    }
}