package pt.ist.meic.phylodb.typing.profile.model;

import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Optional;

public class ProfileInputModel implements InputModel<Profile> {

	private String id;
	private String aka;
	private String[] alleles;

	public ProfileInputModel() {
	}

	public ProfileInputModel(String id, String aka, String[] alleles) {
		this.id = id;
		this.aka = aka;
		this.alleles = alleles;
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

	@Override
	public Optional<Profile> toDomainEntity(String... params) {
		return !params[2].equals(id) || alleles == null ? Optional.empty() :
				Optional.of(new Profile(params[0], params[1], id, aka, alleles));
	}

}
