package pt.ist.meic.phylodb.typing.profile.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label="Profile")
public class Profile {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "identifier")
	private long identifier;

	//@Relationship(type = "DISTANCES")
	//private Set<Distance> distances;

	public Profile() {
	}

	public long getIdentifier() {
		return identifier;
	}
}
