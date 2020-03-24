package pt.ist.meic.phylodb.typing.profile.model.output;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.formatters.dataset.SchemedFileDataset;
import pt.ist.meic.phylodb.formatters.dataset.profile.ProfilesFormatter;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;

import java.util.List;

public class GetProfilesFileOutputModel implements GetProfilesOutputModel<byte[]> {

	private Schema schema;
	private List<Profile> profiles;


	public GetProfilesFileOutputModel(Schema schema, List<Profile> profiles) {
		this.schema = schema;
		this.profiles = profiles;
	}

	@Override
	public ResponseEntity<byte[]> toResponseEntity() {
		ProfilesFormatter formatter = ProfilesFormatter.get(schema.getType());
		String formatted = formatter.format(new SchemedFileDataset(schema, profiles));
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"profiles." + schema.getId().toLowerCase() + "\"")
				.body(formatted.getBytes());
	}

}
