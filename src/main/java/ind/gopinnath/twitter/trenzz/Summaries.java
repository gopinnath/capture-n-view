package ind.gopinnath.twitter.trenzz;

import java.util.ArrayList;
import java.util.List;

public class Summaries {

	private List<HourlySummary> hourlySummaries = new ArrayList<HourlySummary>();

	public List<HourlySummary> getHourlySummaries() {
		return hourlySummaries;
	}

	public void setHourlySummaries(List<HourlySummary> hourlySummaries) {
		this.hourlySummaries = hourlySummaries;
	}

	@Override
	public String toString() {
		return "Summaries [hourlySummaries=" + hourlySummaries + "]";
	}
	
}
