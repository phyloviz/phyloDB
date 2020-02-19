package pt.ist.meic.phylodb.phylogeny.allele.model;

import java.util.List;
import java.util.stream.Collectors;

public class GetAllelesOutputModel {

	private List<GetAlleleOutputModel> alleles;

	public GetAllelesOutputModel(List<Allele> alleles) {
		this.alleles = alleles.stream()
				.map(GetAlleleOutputModel::new)
				.collect(Collectors.toList());
	}

	public List<GetAlleleOutputModel> getAlleles() {
		return alleles;
	}

}
