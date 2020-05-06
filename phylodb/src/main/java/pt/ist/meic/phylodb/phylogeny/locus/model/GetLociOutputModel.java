package pt.ist.meic.phylodb.phylogeny.locus.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetLociOutputModel implements OutputModel {

	private final List<LocusOutputModel> entities;

	public GetLociOutputModel(List<Locus> entities) {
		this.entities = entities.stream()
				.map(LocusOutputModel::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<LocusOutputModel>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
