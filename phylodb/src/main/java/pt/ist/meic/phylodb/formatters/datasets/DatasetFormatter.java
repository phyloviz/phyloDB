package pt.ist.meic.phylodb.formatters.datasets;

import pt.ist.meic.phylodb.formatters.Reader;
import pt.ist.meic.phylodb.formatters.Writer;

import java.util.HashMap;
import java.util.Optional;

public interface DatasetFormatter<T> extends Reader<Dataset<T>>, Writer<Dataset<T>> {
	String FASTA = "fasta", MLST = "mlst", MLVA = "mlva", SNP = "snp";

	static Optional<DatasetFormatter<?>> get(String format) {
		return Optional.ofNullable(new HashMap<String, DatasetFormatter<?>>() {{
			put("fasta", new FastaFormatter());
			put("mlst", new MlstFormatter());
			put("mlva", new MlvaFormatter());
			put("snp", new SnpFormatter());
		}}.get(format));
	}

}
