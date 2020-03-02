package pt.ist.meic.phylodb.phylogeny.allele.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.mediatype.Json;
import pt.ist.meic.phylodb.mediatype.Output;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;

public class GetAlleleOutputModel implements Json, Output<Json> {

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

	@Override
	public ResponseEntity<Json> toResponse() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

}
