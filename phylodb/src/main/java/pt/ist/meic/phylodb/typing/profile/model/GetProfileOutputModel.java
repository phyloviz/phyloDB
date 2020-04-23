package pt.ist.meic.phylodb.typing.profile.model;

import javafx.util.Pair;
import pt.ist.meic.phylodb.phylogeny.allele.model.AlleleOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GetProfileOutputModel extends ProfileOutputModel {

	private String aka;
	private AlleleOutputModel[] alleles;

	public GetProfileOutputModel() {
	}

	public GetProfileOutputModel(Pair<Schema, Profile> pair) {
		super(pair.getValue());
		this.aka = pair.getValue().getAka();
		List<Reference<String>> references = pair.getValue().getAllelesReferences();
		AlleleOutputModel[] alleles = new AlleleOutputModel[references.size()];
		for (int i = 0; i < references.size(); i++) {
			alleles[i] = new AlleleOutputModel(pair.getKey().getPrimaryKey().getTaxonId(),
					pair.getKey().getLociIds().get(i).getPrimaryKey(), references.get(i));
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
