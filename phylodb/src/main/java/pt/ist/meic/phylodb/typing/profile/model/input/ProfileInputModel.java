package pt.ist.meic.phylodb.typing.profile.model.input;

import pt.ist.meic.phylodb.input.Input;
import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.Optional;
import java.util.UUID;

public class ProfileInputModel implements Input<Profile> {

	private UUID datasetId;
	private String id;
	private String aka;
	private String[] alleles;

	public ProfileInputModel() {
	}

	public ProfileInputModel(UUID datasetId, String id, String aka, String[] alleles) {
		this.datasetId = datasetId;
		this.id = id;
		this.aka = aka;
		this.alleles = alleles;
	}

	public UUID getDatasetId() {
		return datasetId;
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
		return !params[0].equals(datasetId.toString()) || !params[1].equals(id) || alleles == null ? Optional.empty() :
				Optional.of(new Profile(datasetId, id, aka, alleles));
	}

}
