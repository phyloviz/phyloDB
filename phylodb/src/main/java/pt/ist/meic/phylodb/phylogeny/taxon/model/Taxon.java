package pt.ist.meic.phylodb.phylogeny.taxon.model;

import pt.ist.meic.phylodb.utils.service.VersionedEntity;

public class Taxon extends VersionedEntity<String> {

	private final String description;

	public Taxon(String id, long version, boolean deprecated, String description) {
		super(id, version, deprecated);
		this.description = description;
	}

	public Taxon(String id, String description) {
		this(id, -1, false, description);
	}

	public String getDescription() {
		return description;
	}

}
