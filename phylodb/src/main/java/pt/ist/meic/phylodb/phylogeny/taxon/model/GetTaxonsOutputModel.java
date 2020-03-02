package pt.ist.meic.phylodb.phylogeny.taxon.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.mediatype.Json;
import pt.ist.meic.phylodb.mediatype.Output;

import java.util.List;
import java.util.stream.Collectors;

public class GetTaxonsOutputModel implements Json, Output<Json> {

	private List<GetTaxonOutputModel> taxons;

	public GetTaxonsOutputModel() {
	}

	public GetTaxonsOutputModel(List<Taxon> taxons) {
		this.taxons = taxons.stream()
				.map(GetTaxonOutputModel::new)
				.collect(Collectors.toList());
	}

	public List<GetTaxonOutputModel> getTaxons() {
		return taxons;
	}

	public void setTaxons(List<GetTaxonOutputModel> taxons) {
		this.taxons = taxons;
	}

	@Override
	public ResponseEntity<Json> toResponse() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}
}
