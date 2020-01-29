package pt.ist.meic.phylodb.phylogeny.locus.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label="Locus")
public class Locus {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name="name")
	private String name;

	public Locus() {
	}

	public String getName() {
		return name;
	}
}
