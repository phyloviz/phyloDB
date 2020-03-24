package pt.ist.meic.phylodb.phylogeny.taxon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.Output;

public class GetTaxonOutputModel implements Json, Output<Json> {

	private DetailedTaxonModel taxon;

	public GetTaxonOutputModel() {
	}

	public GetTaxonOutputModel(Taxon taxon) {
		this.taxon = new DetailedTaxonModel(taxon);
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	private static class DetailedTaxonModel {
		private String id;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String description;

		public DetailedTaxonModel(Taxon taxon) {
			this.id = taxon.getId();
			this.description = taxon.getDescription();
		}

		public String getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}

	}

}
