package pt.ist.meic.phylodb.analysis.visualization.model;

import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.Objects;

/**
 * A coordinate is used to represent the position of a profile within a {@link Visualization visualization}
 * <p>
 * A coordinate is constituted by the {@link #profile}, to identify to which profile the coordinate belongs to,
 * by the {@link #component} to specify the component of the visualization it belongs to, and by the {@link #x} and {@link #y}
 * that are the axis positions.
 */
public class Coordinate {

	private Profile.PrimaryKey profile;
	private long component;
	private double x;
	private double y;

	public Coordinate() {
	}

	public Coordinate(Profile.PrimaryKey profileId, long component, double x, double y) {
		this.profile = profileId;
		this.component = component;
		this.x = x;
		this.y = y;
	}

	public Profile.PrimaryKey getProfile() {
		return profile;
	}

	public long getComponent() {
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
				Objects.equals(profile, that.profile);
	}

}
