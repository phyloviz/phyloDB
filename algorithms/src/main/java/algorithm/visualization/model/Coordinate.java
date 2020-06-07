package algorithm.visualization.model;

public class Coordinate {

	private final String profileId;
	private final double x;
	private final double y;

	public Coordinate(String profileId, double x, double y) {
		this.profileId = profileId;
		this.x = x;
		this.y = y;
	}

	public String getProfileId() {
		return profileId;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}
