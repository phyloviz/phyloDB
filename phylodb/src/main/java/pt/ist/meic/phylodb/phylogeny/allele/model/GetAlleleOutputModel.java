package pt.ist.meic.phylodb.phylogeny.allele.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class GetAlleleOutputModel {

	private String id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String sequence;

	public GetAlleleOutputModel() {
	}

	public GetAlleleOutputModel(Allele locus) {
		this.id = locus.getId();
		this.sequence = locus.getSequence();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

}
