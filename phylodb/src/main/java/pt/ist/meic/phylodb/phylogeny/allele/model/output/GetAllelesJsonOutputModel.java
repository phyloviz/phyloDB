package pt.ist.meic.phylodb.phylogeny.allele.model.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.output.mediatype.Json;
import pt.ist.meic.phylodb.output.model.OutputModel;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;

import java.util.List;
import java.util.stream.Collectors;

public class GetAllelesJsonOutputModel implements Json, GetAllelesOutputModel<Json> {

	private List<SimpleAlleleModel> alleles;

	public GetAllelesJsonOutputModel(List<Allele> alleles) {
		this.alleles = alleles.stream()
				.map(SimpleAlleleModel::new)
				.collect(Collectors.toList());
	}

	public List<SimpleAlleleModel> getAlleles() {
		return alleles;
	}

	@Override
	public ResponseEntity<Json> toResponseEntity() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	@JsonPropertyOrder({"id", "version", "deprecated"})
	private static class SimpleAlleleModel extends OutputModel {

		private String id;

		public SimpleAlleleModel(Allele allele) {
			super(allele.isDeprecated(), allele.getVersion());
			this.id = allele.getId();
		}

		public String getId() {
			return id;
		}

	}

}
