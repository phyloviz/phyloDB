package pt.ist.meic.phylodb.phylogeny.locus.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.mediatype.Json;
import pt.ist.meic.phylodb.mediatype.Output;

import java.util.List;
import java.util.stream.Collectors;

public class GetLociOutputModel implements Json, Output<Json> {

	private List<GetLocusOutputModel> loci;

	public GetLociOutputModel(List<Locus> loci) {
		this.loci = loci.stream()
				.map(GetLocusOutputModel::new)
				.collect(Collectors.toList());
	}

	public List<GetLocusOutputModel> getLoci() {
		return loci;
	}

	@Override
	public ResponseEntity<Json> toResponse() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

}
