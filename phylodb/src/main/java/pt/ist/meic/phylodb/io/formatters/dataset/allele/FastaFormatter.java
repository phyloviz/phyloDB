package pt.ist.meic.phylodb.io.formatters.dataset.allele;

import pt.ist.meic.phylodb.io.formatters.Formatter;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class FastaFormatter extends Formatter<Allele> {

	private String id;
	private StringBuilder sequence;

	@Override
	protected boolean init(Iterator<String> it, Object... params) {
		id = null;
		sequence = new StringBuilder();
		return true;
	}

	@Override
	protected boolean parse(String line, Consumer<Allele> add) {
		if (line.startsWith(">")) {
			String nextId = line.substring(1);
			if (id != null && sequence.length() > 0)
				add.accept(new Allele(id, sequence.toString()));
			id = nextId;
			sequence = new StringBuilder();
		} else if (id != null && line.matches("^[ACTG ]*$")) {
			sequence.append(line);
		} else {
			id = null;
			return false;
		}
		return true;
	}

	@Override
	public String format(List<Allele> alleles, Object... params) {
		StringBuilder rawAlleles = new StringBuilder();
		for (Allele allele : alleles)
			rawAlleles.append(allele.getPrimaryKey().getId())
					.append("\n")
					.append(allele.getSequence())
					.append("\n");
		return rawAlleles.toString();
	}

}
