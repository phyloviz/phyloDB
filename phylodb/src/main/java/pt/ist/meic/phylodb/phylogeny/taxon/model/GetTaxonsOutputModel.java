package pt.ist.meic.phylodb.phylogeny.taxon.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetTaxonsOutputModel implements OutputModel {

	private final List<TaxonOutputModel> entities;

	public GetTaxonsOutputModel(List<Taxon> entities) {
		this.entities = entities.stream()
				.map(TaxonOutputModel::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<TaxonOutputModel>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}



}
