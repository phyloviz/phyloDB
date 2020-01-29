package pt.ist.meic.phylodb.typing.dataset.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.UUID;

@NodeEntity(label = "Dataset")
public class Dataset {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "identifier")
	private UUID identifier;

	@Property(name = "name")
	private String name;

	public UUID getIdentifier() {
		return identifier;
	}

	public String getName() {
		return name;
	}

	public Dataset() {
	}

}
