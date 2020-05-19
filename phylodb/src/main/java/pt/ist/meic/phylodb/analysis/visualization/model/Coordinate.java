package pt.ist.meic.phylodb.analysis.visualization.model;

import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.Objects;

public class Coordinate {

	private Profile.PrimaryKey profile;
	private int x;
	private int y;

	public Coordinate() {
	}

	public Coordinate(Profile.PrimaryKey profileId, int x, int y) {
		this.profile = profileId;
		this.x = x;
		this.y = y;
	}

	public Profile.PrimaryKey getProfile() {
		return profile;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Coordinate that = (Coordinate) o;
		return x == that.x &&
				y == that.y &&
				Objects.equals(profile, that.profile);
	}

}
