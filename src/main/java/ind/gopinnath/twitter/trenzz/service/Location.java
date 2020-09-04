package ind.gopinnath.twitter.trenzz.service;

public class Location {
	
	private String name;
	
	private Long woeid;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getWoeid() {
		return woeid;
	}

	public void setWoeid(Long woeid) {
		this.woeid = woeid;
	}

	@Override
	public String toString() {
		return "Location [name=" + name + ", woeid=" + woeid + "]";
	}
	
}
