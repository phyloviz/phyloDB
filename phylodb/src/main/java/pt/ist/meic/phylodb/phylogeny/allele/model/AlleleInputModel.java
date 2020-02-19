package pt.ist.meic.phylodb.phylogeny.allele.model;

public class AlleleInputModel {

	private String id;
	private String sequence;

	public AlleleInputModel() {
	}

	public AlleleInputModel(String id, String description) {
		this.id = id;
		this.sequence = description;
	}

	public String getId() {
		return id;
	}

	public String getSequence() {
		return sequence;
	}

}
