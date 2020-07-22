package pt.ist.meic.phylodb.typing.isolate.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetIsolatesOutputModel is the output model representation of a set of {@link Isolate isolates}
 * <p>
 * A GetIsolatesOutputModel is constituted by the {@link #isolates} field that contains the resumed information of each isolate.
 * Each resumed information is represented by an {@link IsolateOutputModel.Resumed} object.
 */
public class GetIsolatesOutputModel implements OutputModel {

	private final List<IsolateOutputModel.Resumed> isolates;

	public GetIsolatesOutputModel(List<VersionedEntity<Isolate.PrimaryKey>> entities) {
		this.isolates = entities.stream()
				.map(IsolateOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<IsolateOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(isolates);
	}

}
