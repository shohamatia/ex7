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
        } catch (IOException e) {
            System.err.println(INVALID_INPUT_ARGUMENTS_FILE_ERROR);
            return;
        }
        for (String line:lines)
            System.out.println(line);
    }

    private static List<String> checkLegitFile(String[] args) throws IOException{
        if (args.length != 1) {
            System.err.println(WRONG_NUM_OF_ARGS_ERROR);
            throw new IOException();
        }
        String path_name = args[0];
        Path path = Path.of(path_name);
        return Files.readAllLines(path);
    }




}