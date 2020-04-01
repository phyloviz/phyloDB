package pt.ist.meic.phylodb.typing.profile.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;

import java.util.List;
import java.util.stream.Collectors;

public class GetProfilesJsonOutputModel implements Json, GetProfilesOutputModel<Json> {

	private List<SimpleProfileModel> profiles;

	public GetProfilesJsonOutputModel(List<Profile> profiles) {
		this.profiles = profiles.stream()
				.map(SimpleProfileModel::new)
				.collect(Collectors.toList());
	}

	public GetProfilesJsonOutputModel(Schema ignored, List<Profile> profiles) {
		this(profiles);
	}

	public List<SimpleProfileModel> getProfiles() {
		return profiles;
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	@JsonPropertyOrder({"id", "version", "deprecated"})
	private static class SimpleProfileModel extends OutputModel {

		private String id;

		public SimpleProfileModel(Profile profile) {
			super(profile.isDeprecated(), profile.getVersion());
			this.id = profile.getId();
		}

		public String getId() {
			return id;
		}

	}

}