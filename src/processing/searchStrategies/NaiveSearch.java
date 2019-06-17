package processing.searchStrategies;

import processing.textStructure.Corpus;
import processing.textStructure.WordResult;

import java.util.List;

public class NaiveSearch implements IsearchStrategy {
	private Corpus origin;
	public NaiveSearch(Corpus origin) {
		this.origin = origin;
	}

	
	@Override
	public List<WordResult> search(String query) {
	
	}

}
