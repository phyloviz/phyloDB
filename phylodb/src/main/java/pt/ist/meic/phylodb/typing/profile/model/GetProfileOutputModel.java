package pt.ist.meic.phylodb.typing.profile.model;

import pt.ist.meic.phylodb.io.output.SingleOutputModel;

public class ProfileOutputModel extends SingleOutputModel<Profile.PrimaryKey> {

	private final String aka;
	private final Object[] alleles;

	public ProfileOutputModel(Profile profile) {
		super(profile);
		this.aka = profile.getAka();
		this.alleles = profile.getAllelesReferences().toArray();
	}

	public String getAka() {
		return aka;
	}

	public Object[] getAlleles() {
		return alleles;
	}

}
