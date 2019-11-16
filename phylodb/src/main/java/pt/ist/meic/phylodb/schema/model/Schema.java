package pt.ist.meic.phylodb.schema.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label="Schema")
public class Schema {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "name")
	private String name;
	@Property(name = "tag")
	private String tag;
	@Property(name = "description")
	private String description;

	public Schema() {
	}

	public String getName() {
		return name;
	}

	public String getTag() {
		return tag;
	}

	public String getDescription() {
		return description;
	}
}
