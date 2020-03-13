package pt.ist.meic.phylodb.phylogeny.allele.model.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.mediatype.Json;
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
	public ResponseEntity<Json> toResponse() {
		return ResponseEntity.status(HttpStatus.OK)
				.body(this);
	}

	private static class SimpleAlleleModel {

		private String id;

		public SimpleAlleleModel(Allele allele) {
			this.id = allele.getId();
		}

		public String getId() {
			return id;
		}

	}

}
