package pt.ist.meic.phylodb.phylogeny.allele.model;

import org.neo4j.ogm.annotation.*;

@NodeEntity(label="Allele")
public class Allele {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name="identifier")
	private String identifier;
	@Property(name="sequence")
	private String sequence;

	public Allele() {
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getSequence() {
		return sequence;
	}
}
