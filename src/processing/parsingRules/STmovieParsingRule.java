package processing.parsingRules;

import processing.textStructure.Block;
import processing.textStructure.WordResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class STmovieParsingRule implements IparsingRule, Serializable {
	public static final long serialVersionUID = 1L;

	public STmovieParsingRule() {
	}


	private LinkedList<String[]> getScene(RandomAccessFile file){
		String[] getSceneTitle = new String[4];
		String sceneNumber = null;
		String sceneName = null;
		String sceneStart = null;
		String sceneEnd = null;
		final Pattern p = Pattern.compile(parsingRuleRegex.MOVIE_SCENE_EXTRACTOR);
		final Matcher m;
		//m = p.matcher(file.readLine());
		//if (m.find()){
			//sceneNumber = m.group("sceneNumber");
			//sceneName = m.group("sceneName");
			//int srt = m.start(3);
			//int end = m.end(3);
			//new Block()
		//}
		//getSceneTitle[0] = sceneNumber;
		//getSceneTitle[1] = sceneName;
		return null;
	}

	private String getTillFirstScene(RandomAccessFile file){
		final Pattern sceneLine = Pattern.compile(parsingRuleRegex.SCENE_TITLE);
		final Matcher thisLine = sceneLine.matcher("");
		StringBuilder fileString = null;
		try {
			file.seek(0);
			String nextLine = "";
			String curLine;
			while (thisLine.reset(nextLine).find()){
				curLine = nextLine;
				fileString.append(curLine);
				nextLine = file.readLine();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		return String.valueOf(fileString);
	}

	private String[] getCredits(RandomAccessFile file){
		String fileString = getTillFirstScene(file);
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
		while (m.find()){
			by.add(m.group("whatIsBy") + ": " + m.group("byWhom"));
		}
		return (String[]) by.toArray();
	}

	private String[] getSpeakers(String file){
		LinkedList<String> getSpeaker = new LinkedList<>();
		final Pattern p = Pattern.compile(parsingRuleRegex.MOVIE_SPEAKER);
		final Matcher m = p.matcher(file);
		while (m.find()){
			getSpeaker.add(m.group());
		}
		return (String[]) getSpeaker.toArray();
	}


	@Override
	public Block parseRawBlock(RandomAccessFile inputFile, long startPos, long endPos) {
		return null;
	}

	@Override
	public List<Block> parseFile(RandomAccessFile inputFile) {
		return null;
	}

	@Override
	public void printResult(WordResult wordResult)  {
	
	}
}