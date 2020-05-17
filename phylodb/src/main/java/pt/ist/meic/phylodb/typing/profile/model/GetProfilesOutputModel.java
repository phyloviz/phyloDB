package pt.ist.meic.phylodb.typing.profile.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;

import java.util.List;
import java.util.stream.Collectors;

public class GetProfilesOutputModel implements OutputModel {

	private final List<ProfileOutputModel.Resumed> entities;

	public GetProfilesOutputModel(List<Profile> entities) {
		this.entities = entities.stream()
				.map(ProfileOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<ProfileOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(entities);
	}

}
