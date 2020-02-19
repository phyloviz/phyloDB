package pt.ist.meic.phylodb.typing.ancillary.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label = "Ancillary")
public class Ancillary {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "key")
	private String key;
	@Property(name = "value")
	private String value;

	public Ancillary() {
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
