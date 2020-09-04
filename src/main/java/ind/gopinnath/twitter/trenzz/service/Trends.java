package ind.gopinnath.twitter.trenzz.service;

import java.util.Arrays;

public class Trends {

	private Trend trends[];
	
	private String as_of;
	
	private String created_at;
	
	private Location locations[];

	public Trend[] getTrends() {
		return trends;
	}

	public void setTrends(Trend[] trends) {
		this.trends = trends;
	}

	public String getAs_of() {
		return as_of;
	}

	public void setAs_of(String as_of) {
		this.as_of = as_of;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public Location[] getLocations() {
		return locations;
	}

	public void setLocations(Location[] locations) {
		this.locations = locations;
	}

	@Override
	public String toString() {
		return "Trends [trends=" + Arrays.toString(trends) + ", as_of=" + as_of + ", created_at=" + created_at
				+ ", locations=" + Arrays.toString(locations) + "]";
	}

}
