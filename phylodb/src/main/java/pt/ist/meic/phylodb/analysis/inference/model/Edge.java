package pt.ist.meic.phylodb.analysis.inference.model;

import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.service.Entity;

public class Edge {

	private final Entity<Profile.PrimaryKey> from;
	private final Entity<Profile.PrimaryKey> to;
	private final int weight;

	public Edge(Entity<Profile.PrimaryKey> from, Entity<Profile.PrimaryKey> to, int weight) {
		this.from = from;
		this.to = to;
		this.weight = weight;
	}

	public Entity<Profile.PrimaryKey> getFrom() {
		return from;
	}

	public Entity<Profile.PrimaryKey> getTo() {
		return to;
	}

	public int getWeight() {
		return weight;
	}

}

