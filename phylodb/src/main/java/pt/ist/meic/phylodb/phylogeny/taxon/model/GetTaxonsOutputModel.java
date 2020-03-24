package pt.ist.meic.phylodb.phylogeny.taxon.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.Output;

import java.util.List;
import java.util.stream.Collectors;

public class GetTaxonsOutputModel implements Json, Output<Json> {

	private List<SimpleTaxonModel> taxons;

	public GetTaxonsOutputModel() {
	}

	public GetTaxonsOutputModel(List<Taxon> taxons) {
		this.taxons = taxons.stream()
				.map(SimpleTaxonModel::new)
				.collect(Collectors.toList());
	}

	public List<SimpleTaxonModel> getTaxons() {
		return taxons;
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	private static class SimpleTaxonModel {
		private String id;

		public SimpleTaxonModel(Taxon taxon) {
			this.id = taxon.getId();
		}

		public String getId() {
			return id;
		}

	}
}
