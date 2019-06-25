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

public class STtvSeriesParsingRule implements IparsingRule, Serializable {
	public static final long serialVersionUID = 1L;


	public STtvSeriesParsingRule() {
    }

	public Block parseRawBlock(RandomAccessFile inputFile, long startPos, long endPos) {
		return null;
	}

	private LinkedList<Block> getScene(RandomAccessFile file){
		LinkedList<Block> blocks = new LinkedList<>();
		String sceneNumber;
		String sceneName;
		final Pattern p = Pattern.compile(parsingRuleRegex.TV_SCENE);
		final Matcher m = p.matcher(" ") ;
		getSceneBlock(file, true);
		String curBlock;
		String credits = getCredits(file);
		String header = getHeader(file);
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
					block = new Block(file, curPointer + m.end(2), file.getFilePointer());
					if (file.getFilePointer() >= file.length()){
						throw new IllegalArgumentException();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}catch (IllegalArgumentException e){
					break;
				}
				LinkedList<String> metadata = new LinkedList<>();
				metadata.add("From the episode \"" + header + " of the TV show: \"STAR TREK: " +
						"THE NEXT GENERATION\"");
				metadata.add("Appearing in scene " + sceneNumber + ", titled \"" + sceneName.replaceAll(" " +
						"*$", "") + "\"");
				assert block != null;
				metadata.add(getSpeakers(block.specialToString()));
				metadata.add(credits);
				block.setMetadata(metadata);
				blocks.add(block);

			}
			try {
				curPointer = file.getFilePointer();
				file.seek(blocks.peekLast().getEndIndex());
			} catch (IOException e) {
				e.printStackTrace();
			}
			nextBlock = getSceneBlock(file, false);
		}
		return blocks;
	}



	private String getSceneBlock(RandomAccessFile file, boolean metadata){
		final Pattern sceneLine = Pattern.compile(parsingRuleRegex.TV_SCENE);
		final Matcher thisLine = sceneLine.matcher("");
		StringBuilder fileString = new StringBuilder();
		Boolean readSceneOpening = false;
		try {
			if (metadata){
				file.seek(0);
				readSceneOpening = true;
			}
			if (file.getFilePointer() == file.length() - 1){
				return null;
			}
			String nextLine = "";
			while (file.getFilePointer() < file.length()){
				fileString.append(nextLine);
				long curPointer = file.getFilePointer();
				nextLine = file.readLine() + "\n";
				if (thisLine.reset(nextLine).find()){
					if (readSceneOpening) {
						file.seek(curPointer);
						return String.valueOf(fileString);
					}
					readSceneOpening = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return String.valueOf(fileString);
	}

	private String getHeader(RandomAccessFile file){
		String fileString = getSceneBlock(file, true);
		final Pattern getCreditsPattern = Pattern.compile(parsingRuleRegex.TV_HEADER);
		assert fileString != null;
		final Matcher matcherETitle = getCreditsPattern.matcher(fileString);
		String eTitle = " ";
		if (matcherETitle.find()){
			eTitle = matcherETitle.group(1);
		}
		return eTitle;
	}

	private String getCredits(RandomAccessFile file){
		String fileString = getSceneBlock(file, true);
		final Pattern getCreditsPattern = Pattern.compile(parsingRuleRegex.TV_CREDITS);
		assert fileString != null;
		final Matcher matcherETitle = getCreditsPattern.matcher(fileString);
		String writers = " ";
		if (matcherETitle.find()){
			writers = matcherETitle.group("by1") + " and " + matcherETitle.group("by2");
		}
		return "By: " + writers;
	}

	private String getSpeakers(String file){
		LinkedList<String> getSpeaker = new LinkedList<>();
		final Pattern p = Pattern.compile(parsingRuleRegex.CHARACTERS);
		assert file != null;
		final Matcher m = p.matcher(file);
		while (m.find()){
			if (!getSpeaker.contains("\"" + m.group().replaceAll("[^A-Z]*", "") + "\"")) {
				getSpeaker.add("\"" + m.group().replaceAll("[^A-Z]*", "") + "\"");
			}
		}
		StringBuilder builder = new StringBuilder();
		for (String speaker : getSpeaker){
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

	public List<Block> parseFile(RandomAccessFile inputFile) {
		LinkedList<Block> blocks = getScene(inputFile);
		for (Block block : blocks){
			LinkedList<String> metadata = new LinkedList<>();
			List<String> curMetadata = block.getMetadata();
			metadata.addAll(curMetadata);
			block.setMetadata(metadata);
		}
		return blocks;
	}

	@Override
	public void printResult(WordResult wordResult)  {

	}

}
