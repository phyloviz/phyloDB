package pt.ist.meic.phylodb.typing.isolate.model;

import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class IsolateInputModel implements InputModel<Isolate> {

	private final String id;
	private final String description;
	private final String profileId;
	private final Ancillary[] ancillaries;

	public IsolateInputModel(String id, String description, String profileId, Ancillary[] ancillaries) {
		this.id = id;
		this.description = description;
		this.profileId = profileId;
		this.ancillaries = ancillaries;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getProfileId() {
		return profileId;
	}

	public Ancillary[] getAncillaries() {
		return ancillaries;
	}

	@Override
	public Optional<Isolate> toDomainEntity(String... params) {
		if (!params[1].equals(id)) return Optional.empty();
		Ancillary[] ancillaries = this.ancillaries == null ? new Ancillary[0] : this.ancillaries;
		return Optional.of(new Isolate(UUID.fromString(params[0]), id, description, (Ancillary[]) Arrays.stream(ancillaries).distinct().toArray(), profileId));
	}

}
