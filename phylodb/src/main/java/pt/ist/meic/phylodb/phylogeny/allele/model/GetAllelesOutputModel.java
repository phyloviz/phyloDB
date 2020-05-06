package pt.ist.meic.phylodb.phylogeny.allele.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetAllelesOutputModel implements OutputModel {

	private final List<AlleleOutputModel> entities;

	public GetAllelesOutputModel(List<Allele> entities) {
		this.entities = entities.stream()
				.map(AlleleOutputModel::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<AlleleOutputModel>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
