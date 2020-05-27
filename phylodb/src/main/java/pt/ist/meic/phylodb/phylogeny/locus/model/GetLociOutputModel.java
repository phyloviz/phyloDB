package pt.ist.meic.phylodb.phylogeny.locus.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

public class GetLociOutputModel implements OutputModel {

	private final List<LocusOutputModel.Resumed> entities;

	public GetLociOutputModel(List<VersionedEntity<Locus.PrimaryKey>> entities) {
		this.entities = entities.stream()
				.map(LocusOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<LocusOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
