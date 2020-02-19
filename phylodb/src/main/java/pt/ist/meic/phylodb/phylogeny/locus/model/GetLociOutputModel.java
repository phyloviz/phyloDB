package pt.ist.meic.phylodb.phylogeny.locus.model;

import java.util.List;
import java.util.stream.Collectors;

public class GetLociOutputModel {

	private List<GetLocusOutputModel> loci;

	public GetLociOutputModel(List<Locus> loci) {
		this.loci = loci.stream()
				.map(GetLocusOutputModel::new)
				.collect(Collectors.toList());
	}

	public List<GetLocusOutputModel> getLoci() {
		return loci;
	}

}
