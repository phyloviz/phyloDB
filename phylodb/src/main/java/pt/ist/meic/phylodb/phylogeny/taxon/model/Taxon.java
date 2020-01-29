package pt.ist.meic.phylodb.phylogeny.taxon.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label = "Taxon")
public class Taxon {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "name")
	private String name;

	public String getName() {
		return name;
	}

	public Taxon() {
	}

}
