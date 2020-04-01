package pt.ist.meic.phylodb.phylogeny.taxon.model;

import pt.ist.meic.phylodb.utils.service.Entity;

public class Taxon extends Entity {

	private String id;
	private String description;

	public Taxon(String id, int version, boolean deprecated, String description) {
		super(version, deprecated);
		this.id = id;
		this.description = description;
	}

	public Taxon(String id, String description) {
		super(-1, false);
		this.id = id;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

}
