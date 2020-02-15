package pt.ist.meic.phylodb.phylogeny.taxon.model;

public class Taxon {

	private Long id;
	private String _id;
	private String description;

	public String get_id() {
		return _id;
	}
	public String getDescription() {
		return description;
	}

	public Taxon() {
	}
	public Taxon(String _id, String description) {
		this._id = _id;
		this.description = description;
	}

}
