package pt.ist.meic.phylodb.formatters;

import pt.ist.meic.phylodb.formatters.dataset.*;
import pt.ist.meic.phylodb.formatters.dataset.allele.FastaFormatter;
import pt.ist.meic.phylodb.formatters.dataset.profile.MlstFormatter;
import pt.ist.meic.phylodb.formatters.dataset.profile.MlvaFormatter;
import pt.ist.meic.phylodb.formatters.dataset.profile.SnpFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface Formatter<T> extends Reader<FileDataset<T>>, Writer<FileDataset<T>> {
	String FASTA = "fasta", MLST = "mlst", MLVA = "mlva", SNP = "snp";
	List<String> TYPING = new ArrayList<String>(){{ add(MLST); add(MLVA); add(SNP); }};

	static Optional<Formatter<?>> get(String format) {
		return Optional.ofNullable(new HashMap<String, Formatter<?>>() {{
			put(FASTA, new FastaFormatter());
			put(MLST, new MlstFormatter());
			put(MLVA, new MlvaFormatter());
			put(SNP, new SnpFormatter());
		}}.get(format));
	}

}
