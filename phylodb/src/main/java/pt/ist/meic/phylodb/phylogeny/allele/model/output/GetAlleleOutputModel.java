package pt.ist.meic.phylodb.phylogeny.allele.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.Output;
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

	private static class DetailedAlleleModel {

		private String id;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String sequence;

		public DetailedAlleleModel(Allele allele) {
			this.id = allele.getId();
		}

		public String getId() {
			return id;
		}

		public String getSequence() {
			return sequence;
		}

	}

}
