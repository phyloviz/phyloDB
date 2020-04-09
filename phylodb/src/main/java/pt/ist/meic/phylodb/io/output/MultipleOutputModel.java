package pt.ist.meic.phylodb.io.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.List;

public class MultipleOutputModel implements OutputModel {

	private final SingleOutputModel[] entities;

	public MultipleOutputModel(List<? extends Entity<?>> entities) {
		this.entities = entities.stream()
				.map(s -> new SingleOutputModel(s.getPrimaryKey().toString(), s.getVersion(), s.isDeprecated()))
				.toArray(SingleOutputModel[]::new);
	}

	@Override
	public ResponseEntity<SingleOutputModel[]> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
