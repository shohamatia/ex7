import dataStructures.Aindexer;
import processing.parsingRules.IparsingRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class Configuration {
    String corpusTitle;
    String corpusPathAddress;
    String indexerTitle;
    String indexerTypeString;
    String parseTitle;
    String parseRuleString;
    private String queryTitle;
    private String query;
    private List<String> lines;
    private Boolean hasQuery;
    private Path path;
    private Aindexer.IndexTypes indexType;
    private IparsingRule.ParserTypes parserType;
    private static String line0 = "CORPUS";
    private static String line2 = "INDEXER";
    static HashMap<String, Aindexer.IndexTypes> indexTypesHashMap = new HashMap<>() {{
        for (Aindexer.IndexTypes AnIndexType : Aindexer.IndexTypes.values())
            put(AnIndexType.toString(), AnIndexType);

    }};
    static String line4 = "PARSE_RULE";
    static HashMap<String, IparsingRule.ParserTypes> parserTypesHashMap = new HashMap<>() {{
        for (IparsingRule.ParserTypes AParserType : IparsingRule.ParserTypes.values())
            put(AParserType.toString(), AParserType);
    }};
    static String line6 = "QUERY";
    static String MISSING_REQUIRED_SECTION_ERROR =
            "Error: configuration file does not contain required %s section at line %d.";

    Configuration(List<String> lines) throws IOException {
        this.lines = lines;
        checkLines();
    }

    void checkLines() throws IOException {
        IOException e = new IOException();
        //number of lines
        if (lines.size() != 6 && lines.size() != 8) {
            System.out.println("Error: must be six or eight lines in configuration file.");
            throw e;
        }

        corpusTitle = lines.get(0);
        corpusPathAddress = lines.get(1);
        indexerTitle = lines.get(2);
        indexerTypeString = lines.get(3);
        parseTitle = lines.get(4);
        parseRuleString = lines.get(5);
        hasQuery = false;
        if (lines.size() == 8) {
            hasQuery = true;
            queryTitle = lines.get(6);
            query = lines.get(7);
        }


        // check line 0
        if (!corpusTitle.equals(line0)) {
            System.out.println(String.format(MISSING_REQUIRED_SECTION_ERROR, line0, 0));
            throw e;
        }
        // check line 1
        this.path = Path.of(corpusPathAddress);
        if (!Files.exists(path)) {
            System.out.println("Error: Invalid file path given in line 1 in configuration file.");
            throw e;
        }
        // check line 2
        if (!indexerTitle.equals(line2)) {
            System.out.println(String.format(MISSING_REQUIRED_SECTION_ERROR, line2, 2));
            throw e;
        }
        // check line 3
        indexType = indexTypesHashMap.get(indexerTypeString);
        if (indexType == null) {
            System.out.println("Error: Invalid indexer type given in line 3. Please exchange it with " +
                    "one of the following:");
            indexTypesHashMap.keySet().forEach(System.out::println);
            throw e;
        }
        // check line 4
        if (!parseTitle.equals(line4)) {
            System.out.println(String.format(MISSING_REQUIRED_SECTION_ERROR, line4, 4));
            throw e;
        }
        // check line 5
        parserType = parserTypesHashMap.get(parseRuleString);
        if (parserType == null) {
            System.out.println("Error: Invalid parser type given in line 5. Please exchange it with " +
                    "one of the following:");
            parserTypesHashMap.keySet().forEach(System.err::println);
            throw e;
        }
        // check line 6
        if (hasQuery && !queryTitle.equals(line6)) {
            System.out.println(String.format(MISSING_REQUIRED_SECTION_ERROR, line6, 6));
            throw e;
        }
        // check line 7
        // any string is permissable
    }

    Aindexer.IndexTypes getIndexType() {
        return indexType;
    }

    IparsingRule.ParserTypes getParserType() {
        return parserType;
    }

    String getQuery() throws IllegalStateException {
        if (!hasQuery)
            throw new IllegalStateException();
        return query;
    }

    public String getCorpusPathAddress() {
        return corpusPathAddress;
    }

    public String getIndexerTypeString() {
        return indexerTypeString;
    }

    public String getParseRuleString() {
        return parseRuleString;
    }

    Boolean hasQuery() {
        return hasQuery;
    }

    Path getPath() {
        return path;
    }
}
