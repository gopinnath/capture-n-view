package ind.gopinnath.twitter.trenzz;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HourlySummary {

	private LocalDateTime hour;
	
	private List<TrendResponse> trends = new ArrayList<TrendResponse>();

	public LocalDateTime getHour() {
		return hour;
	}

	public void setHour(LocalDateTime hour) {
		this.hour = hour;
	}

	public List<TrendResponse> getTrends() {
		return trends;
	}

	public void setTrends(List<TrendResponse> trends) {
		this.trends = trends;
	}

	@Override
	public String toString() {
		return "HourlySummary [hour=" + hour + ", trends=" + trends + "]";
	}
	
}
