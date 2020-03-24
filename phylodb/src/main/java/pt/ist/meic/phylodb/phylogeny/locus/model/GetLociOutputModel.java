package pt.ist.meic.phylodb.phylogeny.locus.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.Output;

import java.util.List;
import java.util.stream.Collectors;

public class GetLociOutputModel implements Json, Output<Json> {

	private List<SimpleLocusModel> loci;

	public GetLociOutputModel(List<Locus> loci) {
		this.loci = loci.stream()
				.map(SimpleLocusModel::new)
				.collect(Collectors.toList());
	}

	public List<SimpleLocusModel> getLoci() {
		return loci;
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	private static class SimpleLocusModel {

		private String id;

		public SimpleLocusModel(Locus locus) {
			this.id = locus.getId();
		}

		public String getId() {
			return id;
		}

	}

}
