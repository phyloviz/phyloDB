package pt.ist.meic.phylodb.phylogeny.taxon.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;

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

	@JsonPropertyOrder({"id", "name", "description", "version", "deprecated"})
	private static class DetailedTaxonModel extends OutputModel {
		private String id;
		private String description;

		public DetailedTaxonModel(Taxon taxon) {
			super(taxon.isDeprecated(), taxon.getVersion());
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
