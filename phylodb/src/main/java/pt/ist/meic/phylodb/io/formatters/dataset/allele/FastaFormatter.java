package pt.ist.meic.phylodb.io.formatters.dataset.allele;

import pt.ist.meic.phylodb.io.formatters.Formatter;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class FastaFormatter extends Formatter<Allele> {

	private String taxon;
	private String locus;
	private String project;
	private String id;
	private StringBuilder sequence;

	@Override
	protected boolean init(Iterator<String> it, Object... params) {
		taxon = (String) params[0];
		locus = (String) params[1];
		project = params[2] != null ? (String) params[2] : null;
		id = null;
		sequence = new StringBuilder();
		return true;
	}

	@Override
	protected boolean parse(String line, boolean last, Consumer<Allele> add) {
		if (line.startsWith(">")) {
			if (id != null && sequence.length() > 0)
				add.accept(new Allele(taxon, locus, id, sequence.toString(), project));
			int idIndex = line.lastIndexOf("_");
			if(idIndex == -1 || idIndex == line.length() - 1)
				return false;
			id = line.substring(idIndex + 1);
			sequence = new StringBuilder();
		} else if (id != null && line.matches("^[ACTG ]*$")) {
			sequence.append(line);
		} else {
			id = null;
			sequence = new StringBuilder();
			return false;
		}
		if (last && id != null && sequence.length() > 0)
			add.accept(new Allele(taxon, locus, id, sequence.toString(), project));
		return true;
	}

	@Override
	public String format(List<Allele> alleles, Object... params) {
		StringBuilder formatted = new StringBuilder();
		for (Allele allele : alleles)
			formatted.append(">")
					.append(allele.getPrimaryKey().getLocusId())
					.append("_")
					.append(allele.getPrimaryKey().getId())
					.append("\n")
					.append(formatSequence(allele.getSequence(), (int) params[0]))
					.append("\n");
		return formatted.length() > 0 ? formatted.substring(0, formatted.length() - "\n".length()) : "";
	}

	private String formatSequence(String sequence, int lineLength) {
		if (sequence == null)
			return "";
		StringBuilder formatted = new StringBuilder(sequence);
		int n = sequence.length() / lineLength;
		if (n == 0 || sequence.length() == lineLength)
			return sequence;
		int i = 0;
		while (i != n)
			formatted.insert((i + 1) * lineLength + i++, "\n");
		return formatted.toString();
	}

}
