import dataStructures.Aindexer;
import processing.parsingRules.IparsingRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Configuration {
    List<String> lines;
    Boolean hasQuery;
    Path path;
    Aindexer.IndexTypes indexType;
    IparsingRule.ParserTypes parserType;
    static String line0 = "CORPUS";
    static String line2 = "INDEXER";
    static HashMap<String, Aindexer.IndexTypes> line3Possibilities = new HashMap<>() {{
        put("DICT", Aindexer.IndexTypes.DICT);
        put("NAIVE", Aindexer.IndexTypes.NAIVE);
        put("NAIVE_RK", Aindexer.IndexTypes.NAIVE_RK);
        put("SUFFIX_TREE", Aindexer.IndexTypes.SUFFIX_TREE);
    }};
    static String line4 = "PARSE_RULE";
    static HashMap<String, IparsingRule.ParserTypes> line5Possibilities = new HashMap<>() {{
        put("SIMPLE", IparsingRule.ParserTypes.SIMPLE);
    }};
    //static List<String> line5Possibilities = Arrays.asList("SIMPLE", "ST_MOVIE", "ST_TV");
    static String line6 = "QUERY";
    static String MISSING_REQUIRED_SECTION_ERROR =
            "Error: configuration file does not contain required %s section at line %d.";

    Configuration(List<String> lines) throws IOException {
        this.lines = lines;
        IOException e = new IOException();
        //number of lines
        if (lines.size() != 6 && lines.size() != 8) {
            System.err.println("Error: must be six or eight lines in configuration file.");
            throw e;
        }
        // check line 0
        if (!lines.get(0).equals(line0)) {
            System.err.println(String.format(MISSING_REQUIRED_SECTION_ERROR, line0, 0));
            throw e;
        }
        // check line 1
        this.path = Path.of(lines.get(1));
        if (!Files.exists(path)) {
            System.err.println("Error: Invalid file path given in line 1 in configuration file.");
            throw e;
        }
        // check line 2
        if (!lines.get(2).equals(line2)) {
            System.err.println(String.format(MISSING_REQUIRED_SECTION_ERROR, line2, 2));
            throw e;
        }
        // check line 3
        if (!line3Possibilities.containsKey(lines.get(3))) {
            System.err.println("Error: Invalid indexer type given in line 3. Please exchange it with " +
                    "one of the following:");
            line3Possibilities.keySet().forEach(System.err::println);
            throw e;
        }
        // check line 4
        if (!lines.get(4).equals(line4)) {
            System.err.println(String.format(MISSING_REQUIRED_SECTION_ERROR, line4, 4));
            throw e;
        }
        // check line 5
        if (!line5Possibilities.containsKey(lines.get(5))) {
            System.err.println("Error: Invalid parser type given in line 5. Please exchange it with " +
                    "one of the following:");
            line5Possibilities.keySet().forEach(System.err::println);
            throw e;
        }
        // check line 6
        if (lines.size() == 8 && !lines.get(6).equals(line6)) {
            System.err.println(String.format(MISSING_REQUIRED_SECTION_ERROR, line6, 6));
            throw e;
        }
        // check line 7
        // any string is permissable




    }

}
