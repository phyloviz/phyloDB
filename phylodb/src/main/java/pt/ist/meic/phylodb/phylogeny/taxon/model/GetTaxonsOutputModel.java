package pt.ist.meic.phylodb.phylogeny.taxon.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetTaxonsOutputModel is the output model representation of a set of {@link Taxon taxons}
 * <p>
 * A GetTaxonsOutputModel is constituted by the {@link #taxons} field that contains the resumed information of each taxon.
 * Each resumed information is represented by an {@link TaxonOutputModel.Resumed} object.
 */
public class GetTaxonsOutputModel implements OutputModel {

	private final List<TaxonOutputModel.Resumed> taxons;

	public GetTaxonsOutputModel(List<VersionedEntity<String>> entities) {
		this.taxons = entities.stream()
				.map(TaxonOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<TaxonOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(taxons);
	}

}
