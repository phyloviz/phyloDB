package pt.ist.meic.phylodb.phylogeny.allele.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

public class GetAllelesOutputModel implements OutputModel {

	private final List<AlleleOutputModel.Resumed> entities;

	public GetAllelesOutputModel(List<VersionedEntity<Allele.PrimaryKey>> entities) {
		this.entities = entities.stream()
				.map(AlleleOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<AlleleOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
