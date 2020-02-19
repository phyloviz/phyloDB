package pt.ist.meic.phylodb.typing.dataset.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label = "Dataset")
public class Dataset {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "_id")
	private String _id;

	@Property(name = "name")
	private String name;

	public Dataset() {
	}

	public String get_id() {
		return _id;
	}

	public String getName() {
		return name;
	}

}
