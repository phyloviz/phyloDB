package pt.ist.meic.phylodb.phylogeny.locus.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetLociOutputModel is the output model representation of a set of {@link Locus loci}
 * <p>
 * A GetLociOutputModel is constituted by the {@link #loci} field that contains the resumed information of each locus.
 * Each resumed information is represented by an {@link LocusOutputModel.Resumed} object.
 */
public class GetLociOutputModel implements OutputModel {

	private final List<LocusOutputModel.Resumed> loci;

	public GetLociOutputModel(List<VersionedEntity<Locus.PrimaryKey>> entities) {
		this.loci = entities.stream()
				.map(LocusOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<LocusOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(loci);
	}

}
