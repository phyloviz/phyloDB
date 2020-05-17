package pt.ist.meic.phylodb.typing.isolate.model;

import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.profile.model.ProfileOutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Arrays;
import java.util.Objects;

public class GetIsolateOutputModel extends IsolateOutputModel {

	private String description;
	private Ancillary[] ancillaries;
	private ProfileOutputModel.Resumed profile;

	public GetIsolateOutputModel() {
	}

	public GetIsolateOutputModel(Isolate isolate) {
		super(isolate);
		this.description = isolate.getDescription();
		this.ancillaries = isolate.getAncillaries();
		Entity<Profile.PrimaryKey> profile = isolate.getProfile();
		if(isolate.getProfile() != null)
			this.profile = new ProfileOutputModel.Resumed(profile);
	}

	public String getDescription() {
		return description;
	}

	public Ancillary[] getAncillaries() {
		return ancillaries;
	}

	public ProfileOutputModel.Resumed getProfile() {
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
