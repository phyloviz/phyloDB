package pt.ist.meic.phylodb.typing.profile.model;

import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.allele.model.AlleleOutputModel;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A GetProfileOutputModel is the output model representation of an {@link Profile}
 * <p>
 * A GetProfileOutputModel is constituted by the {@link #project_id}, {@link #dataset_id}, {@link #id} fields to identify the profile,
 * the {@link #deprecated}, and {@link #version} fields which indicates if the profile is deprecated, and what version it has. It is also constituted
 * by the {@link #aka}, that is alternative id for the profile, and by the {@link #alleles} which are the references for the alleles that define this profile.
 */
public class GetProfileOutputModel extends ProfileOutputModel {

	private String aka;
	private AlleleOutputModel.Resumed[] alleles;

	public GetProfileOutputModel() {
	}

	public GetProfileOutputModel(Profile profile) {
		super(profile);
		this.aka = profile.getAka();
		List<VersionedEntity<Allele.PrimaryKey>> references = profile.getAllelesReferences();
		AlleleOutputModel.Resumed[] alleles = new AlleleOutputModel.Resumed[references.size()];
		for (int i = 0; i < references.size(); i++) {
			VersionedEntity<Allele.PrimaryKey> reference = references.get(i);
			if (reference != null)
				alleles[i] = new AlleleOutputModel.Resumed(reference);
		}
		this.alleles = alleles;
	}

	public String getAka() {
		return aka;
	}

	public AlleleOutputModel.Resumed[] getAlleles() {
		return alleles;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		GetProfileOutputModel that = (GetProfileOutputModel) o;
		return super.equals(that) &&
				Objects.equals(aka, that.aka) &&
				Arrays.equals(alleles, that.alleles);
	}

}
