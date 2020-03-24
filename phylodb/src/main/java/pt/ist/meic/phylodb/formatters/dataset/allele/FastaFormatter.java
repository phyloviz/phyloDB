package pt.ist.meic.phylodb.formatters.dataset.allele;

import pt.ist.meic.phylodb.formatters.Formatter;
import pt.ist.meic.phylodb.formatters.dataset.FileDataset;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FastaFormatter implements Formatter<Allele> {

	@Override
	public FileDataset<Allele> parse(Stream<String> data) {
		String[] raw = data.toArray(String[]::new);
		List<Allele> alleles = new ArrayList<>();
		String id = raw[0].substring(1);
		StringBuilder sequence = new StringBuilder();
		for (int i = 1; i < raw.length; i++) {
			if (raw[i].startsWith(">")) {
				alleles.add(new Allele(id, sequence.toString()));
				id = raw[i].substring(1);
				sequence = new StringBuilder();
				continue;
			}
			sequence.append(raw[i]);
		}
		alleles.add(new Allele(id, sequence.toString()));
		return new FileDataset<>(alleles);
	}

	@Override
	public String format(FileDataset<Allele> data) {
		StringBuilder rawAlleles = new StringBuilder();
		for (Allele allele : data.getEntities())
			rawAlleles.append(allele.getId())
					.append("\n")
					.append(allele.getSequence())
					.append("\n");
		return rawAlleles.toString();
	}

}
