package pt.ist.meic.phylodb.typing.isolate.model;

import pt.ist.meic.phylodb.typing.profile.model.ProfileOutputModel;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class GetIsolateOutputModel extends IsolateOutputModel {

	private String description;
	private Ancillary[] ancillaries;
	private ProfileOutputModel profile;

	public GetIsolateOutputModel() {
	}

	public GetIsolateOutputModel(Isolate isolate) {
		super(isolate);
		this.description = isolate.getDescription();
		this.ancillaries = isolate.getAncillaries();
		Reference<String> profile = isolate.getProfile();
		this.profile = new ProfileOutputModel(project_id, dataset_id, isolate.getProfile());
	}

	public String getDescription() {
		return description;
	}

	public Ancillary[] getAncillaries() {
		return ancillaries;
	}

	public ProfileOutputModel getProfile_id() {
		return profile;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		GetIsolateOutputModel that = (GetIsolateOutputModel) o;
		return super.equals(that) &&
				Objects.equals(description, that.description) &&
				Arrays.equals(ancillaries, that.ancillaries) &&
				Objects.equals(profile, that.profile);
	}

}
