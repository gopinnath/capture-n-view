package ind.gopinnath.twitter.trenzz;

public class TrendResponse {

	private Integer trendSequence;
	
	private String name;
	
	private Long tweetVolume;

	public Integer getTrendSequence() {
		return trendSequence;
	}

	public void setTrendSequence(Integer trendSequence) {
		this.trendSequence = trendSequence;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getTweetVolume() {
		return tweetVolume;
	}

	public void setTweetVolume(Long tweetVolume) {
		this.tweetVolume = tweetVolume;
	}

	@Override
	public String toString() {
		return "TrendResponse [trendSequence=" + trendSequence + ", name=" + name + ", tweetVolume=" + tweetVolume
				+ "]";
	}
	
	
}
