package pt.ist.meic.phylodb.typing.profile.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A GetProfilesOutputModel is the output model representation of a set of {@link Profile profiles}
 * <p>
 * A GetProfilesOutputModel is constituted by the {@link #profiles} field that contains the resumed information of each profile.
 * Each resumed information is represented by an {@link ProfileOutputModel.Resumed} object.
 */
public class GetProfilesOutputModel implements OutputModel {

	private final List<ProfileOutputModel.Resumed> profiles;

	public GetProfilesOutputModel(List<VersionedEntity<Profile.PrimaryKey>> entities) {
		this.profiles = entities.stream()
				.map(ProfileOutputModel.Resumed::new)
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<List<ProfileOutputModel.Resumed>> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK).body(profiles);
	}

}
