package pt.ist.meic.phylodb.typing.isolate.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label="Isolate")
public class Isolate {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name="name")
	private String name;

	public Isolate() {
	}

	public String getName() {
		return name;
	}
}
