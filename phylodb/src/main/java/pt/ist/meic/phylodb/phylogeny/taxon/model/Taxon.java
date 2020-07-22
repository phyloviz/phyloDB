package pt.ist.meic.phylodb.phylogeny.taxon.model;

import pt.ist.meic.phylodb.utils.service.VersionedEntity;

/**
 * A taxon is a domain entity to represent a taxonomic unit
 * <p>
 * A taxon is constituted by the {@link #id} field to identify the taxon, the {@link #deprecated} field which indicates if the taxon is deprecated, and
 * the {@link #version} field that is the version of the taxon. It is also constituted by the {@link #description}, that is a description of this taxon.
 */
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
