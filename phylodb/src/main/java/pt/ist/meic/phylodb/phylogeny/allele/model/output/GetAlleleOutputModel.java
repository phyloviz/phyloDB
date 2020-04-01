package pt.ist.meic.phylodb.phylogeny.allele.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;

public class GetAlleleOutputModel implements Json, Output<Json> {

	private DetailedAlleleModel allele;

	public GetAlleleOutputModel() {
	}

	public GetAlleleOutputModel(Allele allele) {
		this.allele = new DetailedAlleleModel(allele);
	}

	public DetailedAlleleModel getAllele() {
		return allele;
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	@JsonPropertyOrder({"id", "sequence", "version", "deprecated"})
	private static class DetailedAlleleModel extends OutputModel {

		private String id;
		private String sequence;

		public DetailedAlleleModel(Allele allele) {
			super(allele.isDeprecated(), allele.getVersion());
			this.id = allele.getId();
			this.sequence = allele.getSequence();
		}

		public String getId() {
			return id;
		}

		public String getSequence() {
			return sequence;
		}

	}

}
