package processing.parsingRules;

//Importing files:
import processing.textStructure.Block;
import processing.textStructure.WordResult;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that defines the parsing of a movie script.
 */
public class STmovieParsingRule implements IparsingRule, Serializable {
    //Constants:
    private static final String IN_SCENE = "Appearing in scene %s, titled \"%s\"";

    public static final long serialVersionUID = 1L;

    public STmovieParsingRule() {
    }


    /**
     * @param file - the entry file
     * @return list of all the blocks in the entry.
     */
    private LinkedList<Block> getScene(RandomAccessFile file) {
        LinkedList<Block> blocks = new LinkedList<>();
        String sceneNumber;
        String sceneName;
        final Pattern p = Pattern.compile(parsingRuleRegex.MOVIE_SCENE_EXTRACTOR);
        final Matcher m = p.matcher(" ");
        getSceneBlock(file, true);
        String nextBlockText;
        long pointer = 0;
        do {
            nextBlockText = getSceneBlock(file, false);
            m.reset(nextBlockText);
            if (!m.find()) {
                continue;
            }
            Block block;
            try {
                block = new Block(file, pointer + m.end(3) - 1, file.getFilePointer());
                pointer = file.getFilePointer();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            } catch (IllegalArgumentException e) {
                break;
            }
            LinkedList<String> metadata = new LinkedList<>();

            //Add metadata:
            sceneName = m.group("sceneName");
            sceneName = sceneName.replaceAll(" *$", "");
            sceneNumber = m.group("sceneNumber");
            metadata.add(String.format(IN_SCENE, sceneNumber, sceneName));
            metadata.add(getSpeakers(block.toString()));
            block.setMetadata(metadata);

            blocks.add(block);

        } while (!Objects.requireNonNull(nextBlockText).isEmpty());
        return blocks;
    }


    /**
     * finds the next block.
     *
     * @param file     - the entry file
     * @param metadata - deferments if to read the beginning of the file or not.
     * @return string of the block
     */
    private String getSceneBlock(RandomAccessFile file, boolean metadata) {
        final Pattern sceneLine = Pattern.compile(parsingRuleRegex.SCENE_TITLE);
        final Matcher thisLine = sceneLine.matcher("");
        StringBuilder fileString = new StringBuilder();
        boolean readASceneOpening = false;
        try {
            if (metadata) {
                file.seek(0);
                readASceneOpening = true;
            }
            if (file.getFilePointer() == file.length() - 1) {
                return null;
            }
            String nextLine = "";
            while (file.getFilePointer() < file.length()) {
                fileString.append(nextLine).append("\n");
                long curPointer = file.getFilePointer();
                nextLine = file.readLine();
                if (thisLine.reset(nextLine).find()) {
                    if (readASceneOpening) {
                        file.seek(curPointer);
                        break;
                    }
                    readASceneOpening = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(fileString);
    }

    /**
     * @param file - the entry file
     * @return list of people that are given credit for this entry.
     */
    private String[] getCredits(RandomAccessFile file) {
        String fileString = getSceneBlock(file, true);
        LinkedList<String> by = new LinkedList<>();
        final Pattern getCreditsPattern = Pattern.compile(parsingRuleRegex.CREDITS);
        assert fileString != null;
        final Matcher matcherCredits = getCreditsPattern.matcher(fileString);
        String credits = " ";
        if (matcherCredits.find()) {
            credits = matcherCredits.group();
        }
        final Pattern p = Pattern.compile(parsingRuleRegex.CREDIT_SPLITTER);
        final Matcher m = p.matcher(credits);
        while (m.find()) {
            by.add(m.group("whatIsBy") + " By: " + "\"" + m.group("byWhom") + "\"");
        }
        return by.toArray(new String[0]);
    }

    /**
     * @param file - the entry file
     * @return string of all the characters in the scene.
     */
    private String getSpeakers(String file) {
        LinkedList<String> getSpeaker = new LinkedList<>();
        final Pattern p = Pattern.compile(parsingRuleRegex.MOVIE_SPEAKER);
        assert file != null;
        final Matcher m = p.matcher(file);
        while (m.find()) {
            if (!getSpeaker.contains("\"" + m.group().replaceAll("[^A-Z]*", "") + "\"")) {
                getSpeaker.add("\"" + m.group().replaceAll("[^A-Z]*", "") + "\"");
            }
        }
        StringBuilder builder = new StringBuilder();
        for (String speaker : getSpeaker) {
            builder.append(speaker).append(", ");
        }
        String speakersString = builder.toString();
        if (speakersString.length() > 2) {
            if (speakersString.substring(speakersString.length() - 2).equals(", ")) {
                speakersString = speakersString.substring(0, speakersString.length() - 2);
            }
        }
        return "With the characters: " + speakersString;
    }

    @Override
    public Block parseRawBlock(RandomAccessFile inputFile, long startPos, long endPos) {
        return null;
    }

    @Override
    public List<Block> parseFile(RandomAccessFile inputFile) {
        StringBuilder entryCredits = new StringBuilder();
        String[] creditsList = getCredits(inputFile);
        for (String credit : creditsList) {
            entryCredits.append(credit).append(", ");
        }
        String credits = entryCredits.toString();
        if (credits.length() > 2) {
            if (credits.substring(credits.length() - 2).equals(", ")) {
                credits = credits.substring(0, credits.length() - 2);
            }
        }
        LinkedList<Block> blocks = getScene(inputFile);
        for (Block block : blocks) {
            LinkedList<String> metadata = new LinkedList<>(block.getMetadata());
            metadata.add(credits);
            block.setMetadata(metadata);
        }
        return blocks;
    }

    @Override
    public void printResult(WordResult wordResult) {

    }
}