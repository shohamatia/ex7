package processing.parsingRules;

public class parsingRuleRegex {
    static final String SCENE_TITLE = "(?m)(?<sceneNumber>\\d+[A-Z]*)\\s+(?<sceneName>.*?)\\s+" +
            "(\\k<sceneNumber>)";
    static final String CREDITS = "^(\\s*(?<whatIsBy>\\w+) by:\\s*?(?:\\r\\n?|\\n)\\s+(?<byWhom>" +
            "(?:[^\\r\\n]+)+))+";
    static final String CREDIT_SPLITTER = "(\\s*(?<whatIsBy>\\w+) by:\\s*?(?:\\r\\n?|\\n)\\s+(?<byWhom>" +
            "(?:[^\\r\\n]+)+))";
    static final String SPEAKER = "?:^\\s+)(?<name>[A-Z]+)(?:$|(?:'S)?\\sVOICE)";
    static final String MOVIE_SPEAKER = "\\s{43}((?:\\S+\\s))+";
    static final String MOVIE_SCENE_EXTRACTOR = "(?m)(?<sceneNumber>\\d+[A-Z]*)\\s+(?<sceneName>.*?)\\s+" +
            "(\\k<sceneNumber>)\\s*\\R(\\s{19}.*)*\\R";
    static final String MOVIE_SCENE_HEADER = "\\s+(?<sceneNumber>\\d+)\\s+(?<sceneName>.*)" +
            "\\s+\\k<sceneNumber>\\s+(.*)";
    static final String TV_HEADER = "(?m)\\A\\s+STAR TREK: THE NEXT GENERATION\\s+\"" +
            "(?<episodeTitle>[^\\\"]+)\"\\s+([^\\r\\n])\" +\"*by\\s+(?<by_1>[^\\r\\n]+)\\s+(?:and\\s+" +
            "(?<by2>[^\\r\\n]+))*\\";
    static final String TV_SCENE = "(?<sceneNumber>\\d+)\\s+(?<sceneName>[^\\r\\n]+)\\R";
    static final String CHARACTERS = "^\\t{5}(?<character>[^\\t\\r\\n]+)[\\r\\n]";
}
