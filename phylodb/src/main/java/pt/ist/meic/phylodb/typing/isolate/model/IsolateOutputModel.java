package pt.ist.meic.phylodb.typing.isolate.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;
import pt.ist.meic.phylodb.utils.service.Reference;

public class IsolateOutputModel extends SingleOutputModel {

	private final String description;
	private final Ancillary[] ancillaries;
	private final String profile_id;
	private final int profile_version;
	private final boolean profile_deprecated;

	public IsolateOutputModel(Isolate isolate) {
		super(isolate.getPrimaryKey().getId(), isolate.getVersion(), isolate.isDeprecated());
		this.description = isolate.getDescription();
		this.ancillaries = isolate.getAncillaries();
		Reference<String> profile = isolate.getProfile();
		this.profile_id = profile.getPrimaryKey();
		this.profile_version = profile.getVersion();
		this.profile_deprecated = profile.isDeprecated();
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

	public boolean isProfile_deprecated() {
		return profile_deprecated;
	}

}
