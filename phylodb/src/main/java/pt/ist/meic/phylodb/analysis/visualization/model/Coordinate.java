package pt.ist.meic.phylodb.analysis.visualization.model;

import pt.ist.meic.phylodb.typing.profile.model.Profile;

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

}
