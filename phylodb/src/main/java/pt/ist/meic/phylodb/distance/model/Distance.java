package pt.ist.meic.phylodb.distance.model;

import org.neo4j.ogm.annotation.*;
import pt.ist.meic.phylodb.profile.model.Profile;

@RelationshipEntity(type = "DISTANCES")
public class Distance {

	@Id
	@GeneratedValue
	private Long relationshipId;

	@Property(name="algorithm")
	private String algorithm;
	@Property(name="weigth")
	private String weigth;
	@StartNode
	private Profile from;
	@EndNode
	private Profile to;

	public Distance() {
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public String getWeigth() {
		return weigth;
	}

	public Profile getFrom() {
		return from;
	}

	public Profile getTo() {
		return to;
	}
}
