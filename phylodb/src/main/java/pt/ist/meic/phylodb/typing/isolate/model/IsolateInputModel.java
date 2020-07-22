package pt.ist.meic.phylodb.typing.isolate.model;

import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Arrays;
import java.util.Optional;

/**
 * An IsolateInputModel is the input model for an isolate
 * <p>
 * An IsolateInputModel is constituted by the {@link #id} field to identify the isolate,
 * by the {@link #description}, that is a description of this isolate, by the {@link #ancillaries}
 * which are a set of details associated to the isolate, and by the {@link #profileId} which is the profile that this isolate is associated with.
 */
public class IsolateInputModel implements InputModel<Isolate> {

	private String id;
	private String description;
	private String profileId;
	private Ancillary[] ancillaries;

	public IsolateInputModel() {
	}

	public IsolateInputModel(String id, String description, Ancillary[] ancillaries, String profileId) {
		this.id = id;
		this.description = description;
		this.ancillaries = ancillaries;
		this.profileId = profileId;
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

	public String getProfileId() {
		return profileId;
	}

	@Override
	public Optional<Isolate> toDomainEntity(String... params) {
		if (!params[2].equals(id)) return Optional.empty();
		Ancillary[] ancillaries = this.ancillaries == null ? new Ancillary[0] : Arrays.stream(this.ancillaries)
				.filter(a -> a.getKey() != null && a.getValue() != null)
				.distinct()
				.toArray(Ancillary[]::new);
		return Optional.of(new Isolate(params[0], params[1], id, description, ancillaries, profileId));
	}

}
