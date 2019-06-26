//importing files:
import dataStructures.Aindexer;
import processing.parsingRules.IparsingRule;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 * Class that helps with running the main method.
 */
public class Configuration {
    //Defining constants:
    private final static String CORPUS = "CORPUS";
    private final static String INDEXER = "INDEXER";
    private final static String PARSE_RULE = "PARSE_RULE";
    private final static String QUERY = "QUERY";
    private final static String MISSING_REQUIRED_SECTION_ERROR = "Error: configuration file does not " +
            "contain required %s section at line %d.";
    private final static String WRONG_NUMBER_OF_INPUTS = "Error: must be six or eight lines in " +
            "configuration file.";

    //Defining valuables:
    private String corpusPathAddress;
    private String parseRuleString;
    private String queryTitle;
    private String query;
    private List<String> lines;
    private Boolean hasQuery;
    private Path path;
    private Aindexer.IndexTypes indexType;
    private static HashMap<String, Aindexer.IndexTypes> indexTypesHashMap =
            new HashMap<String, Aindexer.IndexTypes>() {{
                for (Aindexer.IndexTypes AnIndexType : Aindexer.IndexTypes.values())
                    put(AnIndexType.toString(), AnIndexType);
            }};
    private static HashMap<String, IparsingRule.ParserTypes> parserTypesHashMap = new HashMap<String,
            IparsingRule.ParserTypes>() {{
        for (IparsingRule.ParserTypes AParserType : IparsingRule.ParserTypes.values())
            put(AParserType.toString(), AParserType);
    }};

    /**
     * Constructor
     * @param lines - The lines of text in the input file.
     * @throws IllegalArgumentException - throws exception if the input file doesn't match the requirements.
     */
    Configuration(List<String> lines) throws IllegalArgumentException {
        this.lines = lines;
        checkLines();
    }

    /**
     * Checks the input file
     * @throws IllegalArgumentException - throws exception if the input file doesn't match the requirements.
     */
    private void checkLines() throws IllegalArgumentException {
        //number of lines
        if (lines.size() != 6 && lines.size() != 8) {
            throw new IllegalArgumentException(WRONG_NUMBER_OF_INPUTS);
        }

        String corpusTitle = lines.get(0);
        corpusPathAddress = lines.get(1);
        String indexerTitle = lines.get(2);
        String indexerTypeString = lines.get(3);
        String parseTitle = lines.get(4);
        parseRuleString = lines.get(5);
        hasQuery = false;
        if (lines.size() == 8) {
            hasQuery = true;
            queryTitle = lines.get(6);
            query = lines.get(7);
        }


        // check line 0
        if (!corpusTitle.equals(CORPUS)) {
            throw new IllegalArgumentException(String.format(MISSING_REQUIRED_SECTION_ERROR, CORPUS, 0));
        }
        // check line 1
        this.path = Paths.get(corpusPathAddress);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Error: Invalid file path given in line 1 in configuration file.");
        }
        // check line 2
        if (!indexerTitle.equals(INDEXER)) {
            throw new IllegalArgumentException(String.format(MISSING_REQUIRED_SECTION_ERROR, INDEXER, 2));
        }
        // check line 3
        indexType = indexTypesHashMap.get(indexerTypeString);
        if (indexType == null) {
            StringBuilder stringBuilder = new StringBuilder("Error: Invalid indexer type given in line 3. Please exchange it with " +
                    "one of the following:\n");
            for (String s : indexTypesHashMap.keySet())
                stringBuilder.append(s).append("\n");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        // check line 4
        if (!parseTitle.equals(PARSE_RULE)) {
            throw new IllegalArgumentException(String.format(MISSING_REQUIRED_SECTION_ERROR, PARSE_RULE, 4));
        }
        // check line 5
        IparsingRule.ParserTypes parserType = parserTypesHashMap.get(parseRuleString);
        if (parserType == null) {
            StringBuilder stringBuilder = new StringBuilder("Error: Invalid parser type given in line 5. Please " +
                    "exchange it with one of the following:");
            for (String s : parserTypesHashMap.keySet())
                stringBuilder.append(s).append("\n");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        // check line 6
        if (hasQuery && !queryTitle.equals(QUERY)) {
            throw new IllegalArgumentException(String.format(MISSING_REQUIRED_SECTION_ERROR, QUERY, 6));
        }
        // check line 7
        // any string is permissible
    }

    /**
     * @return - The indexer type.
     */
    Aindexer.IndexTypes getIndexType() {
        return indexType;
    }

    /**
     * @return - The query if there is one.
     */
    String getQuery() {
        if (!hasQuery)
            return null;
        return query;
    }

    /**
     * @return - The path of the files as string.
     */
    String getCorpusPathAddress() {
        return corpusPathAddress;
    }

    /**
     * @return - The parse rule as string.
     */
    String getParseRuleString() {
        return parseRuleString;
    }

    /**
     * @return true if has query false otherwise.
     */
    Boolean hasQuery() {
        return hasQuery;
    }

    /**
     * @return - The path of the files as path object.
     */
    Path getPath() {
        return path;
    }
}
