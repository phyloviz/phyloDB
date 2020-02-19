package pt.ist.meic.phylodb.phylogeny.locus.model;

public class LocusInputModel {

	private String id;
	private String description;

	public LocusInputModel() {
	}

	public LocusInputModel(String id, String description) {
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
