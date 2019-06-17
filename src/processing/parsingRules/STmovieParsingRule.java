package processing.parsingRules;

import processing.textStructure.Block;
import processing.textStructure.WordResult;

import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.List;


public class STmovieParsingRule implements IparsingRule, Serializable {
	public static final long serialVersionUID = 1L;

	public STmovieParsingRule() {
	}



	@Override
	public Block parseRawBlock(RandomAccessFile inputFile, long startPos, long endPos) {

	}

	@Override
	public List<Block> parseFile(RandomAccessFile inputFile) {

	}

	@Override
	public void printResult(WordResult wordResult)  {
	
	}
}