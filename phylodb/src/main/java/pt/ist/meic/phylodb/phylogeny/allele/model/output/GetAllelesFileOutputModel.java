package pt.ist.meic.phylodb.phylogeny.allele.model.output;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pt.ist.meic.phylodb.formatters.datasets.Dataset;
import pt.ist.meic.phylodb.formatters.datasets.FastaFormatter;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;

import java.util.List;

public class GetAllelesFileOutputModel implements GetAllelesOutputModel<byte[]> {

	private List<Allele> alleles;

	public GetAllelesFileOutputModel(List<Allele> alleles) {
		this.alleles = alleles;
	}

	@Override
	public ResponseEntity<byte[]> toResponse() {
		String fasta = new FastaFormatter().format(new Dataset<>(alleles));
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"alleles.fasta\"")
				.body(fasta.getBytes());
	}

}
