package pt.ist.meic.phylodb.phylogeny.locus.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.Output;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;

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

	@JsonPropertyOrder({"id", "version", "deprecated"})
	private static class SimpleLocusModel extends OutputModel {

		private String id;

		public SimpleLocusModel(Locus locus) {
			super(locus.isDeprecated(), locus.getVersion());
			this.id = locus.getId();
		}

		public String getId() {
			return id;
		}

	}

}
