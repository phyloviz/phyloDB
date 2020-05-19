package pt.ist.meic.phylodb.analysis.inference.model;

import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Edge edge = (Edge) o;
		return weight == edge.weight &&
				Objects.equals(from, edge.from) &&
				Objects.equals(to, edge.to);
	}

}

