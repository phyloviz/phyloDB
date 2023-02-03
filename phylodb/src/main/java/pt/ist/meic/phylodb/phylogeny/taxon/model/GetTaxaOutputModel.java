package pt.ist.meic.phylodb.phylogeny.taxon.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetTaxaOutputModel is the output model representation of a set of {@link Taxon taxa}
 * <p>
 * A GetTaxaOutputModel is constituted by the {@link #taxa} field that contains the resumed information of each taxon.
 * Each resumed information is represented by an {@link TaxonOutputModel.Resumed} object.
 */
public class GetTaxaOutputModel implements OutputModel {

	private final List<TaxonOutputModel.Resumed> taxa;

	public GetTaxaOutputModel(List<VersionedEntity<String>> entities) {
		this.taxa = entities.stream()
				.map(TaxonOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<TaxonOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(taxa);
	}

}
