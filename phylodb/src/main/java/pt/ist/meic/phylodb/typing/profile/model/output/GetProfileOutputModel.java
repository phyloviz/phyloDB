package pt.ist.meic.phylodb.typing.profile.model.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.typing.profile.model.Profile;

public class GetProfileOutputModel implements Json, Output<Json> {

	private DetailedAlleleModel profile;

	public GetProfileOutputModel() {
	}

	public GetProfileOutputModel(Profile profile) {
		this.profile = new DetailedAlleleModel(profile);
	}

	public DetailedAlleleModel getProfile() {
		return profile;
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	private static class DetailedAlleleModel {

		private String id;
		private String aka;
		private String[] alleles;

		public DetailedAlleleModel(Profile profile) {
			this.id = profile.getId();
			this.aka = profile.getAka();
			this.alleles = profile.getAllelesIds();
		}

		public String getId() {
			return id;
		}

		public String getAka() {
			return aka;
		}

		public String[] getAlleles() {
			return alleles;
		}

	}

}
