package pt.ist.meic.phylodb.phylogeny.allele.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetAllelesOutputModel is the output model representation of a set of {@link Allele alleles}
 * <p>
 * A GetAllelesOutputModel is constituted by the {@link #alleles} field that contains the resumed information of each allele.
 * Each resumed information is represented by an {@link AlleleOutputModel.Resumed} object.
 */
public class GetAllelesOutputModel implements OutputModel {

	private final List<AlleleOutputModel.Resumed> alleles;

	public GetAllelesOutputModel(List<VersionedEntity<Allele.PrimaryKey>> entities) {
		this.alleles = entities.stream()
				.map(AlleleOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<AlleleOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(alleles);
	}

}
