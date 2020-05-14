package pt.ist.meic.phylodb.analysis.visualization.model;

import pt.ist.meic.phylodb.typing.profile.model.Profile;

public class Coordinate {

	private final Profile.PrimaryKey profileId;
	private final int x;
	private final int y;

	public Coordinate(Profile.PrimaryKey profileId, int x, int y) {
		this.profileId = profileId;
		this.x = x;
		this.y = y;
	}

	public Profile.PrimaryKey getProfileId() {
		return profileId;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
