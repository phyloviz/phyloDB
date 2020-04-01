package pt.ist.meic.phylodb.typing.profile.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.typing.profile.model.Profile;

public class GetProfileOutputModel implements Json, Output<Json> {

	private DetailedProfileModel profile;

	public GetProfileOutputModel() {
	}

	public GetProfileOutputModel(Profile profile) {
		this.profile = new DetailedProfileModel(profile);
	}

	public DetailedProfileModel getProfile() {
		return profile;
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	@JsonPropertyOrder({ "id", "version", "deprecated", "aka", "alleles"})
	private static class DetailedProfileModel extends OutputModel {

		private String id;
		private String aka;
		private Object[] alleles;

		public DetailedProfileModel(Profile profile) {
			super(profile.isDeprecated(), profile.getVersion());
			this.id = profile.getId();
			this.aka = profile.getAka();
			this.alleles = profile.getAllelesIds().toArray();
		}

		public String getId() {
			return id;
		}

		public String getAka() {
			return aka;
		}

		public Object[] getAlleles() {
			return alleles;
		}

	}

}
