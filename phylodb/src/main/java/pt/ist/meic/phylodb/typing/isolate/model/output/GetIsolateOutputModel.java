package pt.ist.meic.phylodb.typing.isolate.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.typing.isolate.model.Ancillary;
import pt.ist.meic.phylodb.typing.isolate.model.Isolate;
import pt.ist.meic.phylodb.utils.service.Reference;

public class GetIsolateOutputModel implements Json, Output<Json> {

	private DetailedIsolateModel isolate;


	public GetIsolateOutputModel(Isolate isolate) {
		this.isolate = new DetailedIsolateModel(isolate);
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	@JsonPropertyOrder({ "id", "version", "deprecated", "description", "ancillaries", "profile_id", "profile_version", "profile_deprecated" })
	private static class DetailedIsolateModel extends OutputModel {

		private String id;
		private String description;
		private Ancillary[] ancillaries;
		private String profile_id;
		private int profile_version;
		private boolean profile_deprecated;

		public DetailedIsolateModel(Isolate isolate) {
			super(isolate.isDeprecated(), isolate.getVersion());
			this.id = isolate.getId();
			this.description = isolate.getDescription();
			this.ancillaries = isolate.getAncillaries();
			Reference<String> profile = isolate.getProfile();
			this.profile_id = profile.getId();
			this.profile_version = profile.getVersion();
			this.profile_deprecated = profile.isDeprecated();
		}

		public String getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}

		public Ancillary[] getAncillaries() {
			return ancillaries;
		}

		public String getProfile_id() {
			return profile_id;
		}

		public int getProfile_version() {
			return profile_version;
		}

		public boolean getProfile_deprecated() {
			return profile_deprecated;
		}

	}
}
