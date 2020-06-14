package algorithm.visualization.model;

import java.util.Objects;

public class Coordinate {

	private final String profileId;
	private final int component;
	private final double x;
	private final double y;

	public Coordinate(String profileId, int component, double x, double y) {
		this.profileId = profileId;
		this.component = component;
		this.x = x;
		this.y = y;
	}

	public String getProfileId() {
		return profileId;
	}

	public int getComponent() {
		return component;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Coordinate that = (Coordinate) o;
		return component == that.component &&
				Double.compare(that.x, x) == 0 &&
				Double.compare(that.y, y) == 0 &&
				Objects.equals(profileId, that.profileId);
	}

}
