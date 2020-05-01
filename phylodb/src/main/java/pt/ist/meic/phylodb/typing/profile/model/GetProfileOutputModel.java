package pt.ist.meic.phylodb.typing.profile.model;

import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.allele.model.AlleleOutputModel;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GetProfileOutputModel extends ProfileOutputModel {

	private String aka;
	private AlleleOutputModel[] alleles;

	public GetProfileOutputModel() {
	}

	public GetProfileOutputModel(Profile profile) {
		super(profile);
		this.aka = profile.getAka();
		List<Entity<Allele.PrimaryKey>> references = profile.getAllelesReferences();
		AlleleOutputModel[] alleles = new AlleleOutputModel[references.size()];
		for (int i = 0; i < references.size(); i++) {
			Entity<Allele.PrimaryKey> reference = references.get(i);
			if(reference != null)
				alleles[i] = new AlleleOutputModel(reference);
		}
		this.alleles = alleles;
	}

	public String getAka() {
		return aka;
	}

	public AlleleOutputModel[] getAlleles() {
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
