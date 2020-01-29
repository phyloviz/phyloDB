package pt.ist.meic.phylodb.typing.profile.model;

import org.neo4j.ogm.annotation.*;
import pt.ist.meic.phylodb.analysis.inference.model.Distance;

import java.util.Set;

@NodeEntity(label="Profile")
public class Profile {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "identifier")
	private long identifier;

	@Relationship(type = "DISTANCES")
	private Set<Distance> distances;

	public Profile() {
	}

	public long getIdentifier() {
		return identifier;
	}
}
