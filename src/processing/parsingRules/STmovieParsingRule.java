package processing.parsingRules;

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


public class STmovieParsingRule implements IparsingRule, Serializable {
	public static final long serialVersionUID = 1L;

	public STmovieParsingRule() {
	}


	private LinkedList<Block> getScene(RandomAccessFile file){
		LinkedList<Block> blocks = new LinkedList<>();
		String sceneNumber;
		String sceneName;
		final Pattern p = Pattern.compile(parsingRuleRegex.MOVIE_SCENE_HEADER);
		final Matcher m = p.matcher(" ") ;
		getSceneBlock(file, true);
		String curBlock;
		String nextBlock = getSceneBlock(file, false);
		long curPointer = 0;
		while (!Objects.requireNonNull(nextBlock).isEmpty()){
			curBlock = nextBlock;
			m.reset(curBlock);
			if (m.find()){
				sceneName = m.group("sceneName");
				sceneNumber = m.group("sceneNumber");
				Block block = null;
				try {
					block = new Block(file, curPointer + m.start(3), file.getFilePointer());
					if (file.getFilePointer() >= file.length()){
						throw new IllegalArgumentException();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}catch (IllegalArgumentException e){
					break;
				}
				LinkedList<String> metadata = new LinkedList<>();
				metadata.add("Appearing in scene " + sceneNumber + ", titled \"" + sceneName.replaceAll(" " +
						"*$", "") + "\"");
				metadata.add("Taken out of the entry \"");
				metadata.add(getSpeakers(m.group(3)));
				block.setMetadata(metadata);
				blocks.add(block);
			}
			try {
				curPointer = file.getFilePointer();
			} catch (IOException e) {
				e.printStackTrace();
			}
			nextBlock = getSceneBlock(file, false);
		}
		return blocks;
	}



	private String getSceneBlock(RandomAccessFile file, boolean metadata){
		final Pattern sceneLine = Pattern.compile(parsingRuleRegex.SCENE_TITLE);
		final Matcher thisLine = sceneLine.matcher("");
		StringBuilder fileString = new StringBuilder();
		int cunt = 0;
		try {
			if (metadata){
				file.seek(0);
				cunt = 1;
			}
			if (file.getFilePointer() == file.length() - 1){
				return null;
			}
			String nextLine = "";
			String curLine;
			while (cunt != 2 && file.getFilePointer() < file.length()){
				curLine = nextLine;
				fileString.append(curLine);
				long curPointer = file.getFilePointer();
				nextLine = file.readLine();
				if (thisLine.reset(nextLine).find()){
					if (cunt == 1) {
						file.seek(curPointer);
					}
					cunt += 1;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return String.valueOf(fileString);
	}

	private String[] getCredits(RandomAccessFile file){
		String fileString = getSceneBlock(file, true);
		LinkedList<String> by = new LinkedList<>();
		final Pattern getCreditsPattern = Pattern.compile(parsingRuleRegex.CREDITS);
		assert fileString != null;
		final Matcher matcherCredits = getCreditsPattern.matcher(fileString);
		String credits = " ";
		if (matcherCredits.find()){
			credits = matcherCredits.group();
		}
		final Pattern p = Pattern.compile(parsingRuleRegex.CREDIT_SPLITTER);
		final Matcher m = p.matcher(credits);
		while (m.find()) {
			by.add(m.group("whatIsBy") + " By: " + "\"" + m.group("byWhom") + "\"");
		}
		return by.toArray(new String[0]);
	}

	private String getSpeakers(String file){
		StringBuilder getSpeaker = new StringBuilder();
		final Pattern p = Pattern.compile(parsingRuleRegex.MOVIE_SPEAKER);
		assert file != null;
		final Matcher m = p.matcher(file);
		while (m.find()){
			getSpeaker.append(m.group()).append(" ,");
		}
		String speakersString = getSpeaker.toString();
		if (speakersString.length() > 2) {
			if (speakersString.substring(speakersString.length() - 2).equals(" ,")) {
				speakersString = speakersString.substring(0, speakersString.length() - 2);
			}
		}
		return speakersString;
	}

	public Block parseRawBlock(RandomAccessFile inputFile, long startPos, long endPos) {
		return null;
	}

	public List<Block> parseFile(RandomAccessFile inputFile) {
		StringBuilder entryCredits = new StringBuilder();
		String[] creditsList = getCredits(inputFile);
		for (String credit : creditsList){
			entryCredits.append(credit).append(", ");
		}
		String credits = entryCredits.toString();
		if (credits.length() > 2) {
            if (credits.substring(credits.length() - 2).equals(", ")) {
                credits = credits.substring(0, credits.length() - 2);
            }
        }
		LinkedList<Block> blocks = getScene(inputFile);
		for (Block block : blocks){
			LinkedList<String> metadata = new LinkedList<>();
			List<String> curMetadata = block.getMetadata();
			metadata.addAll(curMetadata);
			metadata.add(credits);
			block.setMetadata(metadata);
		}
		return blocks;
	}

	public void printResult(WordResult wordResult)  {
	
	}
}