import processing.searchStrategies.NaiveSearch;
import processing.textStructure.Corpus;
import processing.textStructure.WordResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The main program - A text searching module that indexes and queries large corpuses for strings or word groups
 */
public class TextSearcher {
    static String WRONG_NUM_OF_ARGS_ERROR = "This function should only receive a single argument.";
    static String INVALID_INPUT_ARGUMENTS_FILE_ERROR = "The input was an invalid file.";

    /**
     * Main method. Reads and parses a command file and if a query exists, prints the results.
     *
     * @param args
     */
    public static void main(String[] args) {
        List<String> lines;
        try {
            lines = checkLegitFile(args);
            Configuration conf = new Configuration(lines);
            testConfiguration(conf);
            Corpus origin = new Corpus(conf.getCorpusPathAddress(), conf.getParseRuleString());
            NaiveSearch naiveSearch = new NaiveSearch(origin);
            if (!conf.hasQuery()) {
                return;
            }
            try {
                String query = conf.getQuery();
                List<WordResult> results = naiveSearch.search(query);
                results.forEach(System.out::println);
            } catch (IllegalStateException e) {
                System.out.println("Something went wrong");
            }
        } catch (IOException e) {
            System.out.println(INVALID_INPUT_ARGUMENTS_FILE_ERROR);
            return;
        }
        //lines.forEach(System.out::println);
    }

    private static List<String> checkLegitFile(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println(WRONG_NUM_OF_ARGS_ERROR);
            throw new IOException();
        }
        String path_name = args[0];
        Path path = Path.of(path_name);
        return Files.readAllLines(path);
    }

    static void testConfiguration(Configuration conf) {
        System.out.println(conf.getIndexType().toString());
        System.out.println(conf.getParserType().toString());
        System.out.println(conf.getPath().toString());
        try {
            System.out.println(conf.getQuery());
        } catch (Exception e) {
        }

    }


}