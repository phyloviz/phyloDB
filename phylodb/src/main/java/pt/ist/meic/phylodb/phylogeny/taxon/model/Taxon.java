package pt.ist.meic.phylodb.phylogeny.taxon.model;

public class Taxon {

	private String id;
	private String description;

	public Taxon() {
	}

	public Taxon(String id, String description) {
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
