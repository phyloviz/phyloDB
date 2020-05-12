package pt.ist.meic.phylodb.io.formatters.analysis;

import pt.ist.meic.phylodb.analysis.inference.model.Edge;

import java.util.List;
import java.util.function.Consumer;

public class NexusFormatter extends NewickFormatter {

	private boolean trees;

	@Override
	public boolean parse(String line, boolean last, Consumer<Edge> add) {
		if(line.equals("BEGIN TREES;")) {
			trees = true;
			return true;
		} else if(line.equals("END;")) {
			trees = false;
			return true;
		} else if(trees) {
			if(!line.contains("("))
				return true;
			String current = line.substring(line.indexOf('('));
			return super.parse(current, last, add);
		} else {
			return true;
		}
	}

	@Override
	public String format(List<Edge> entities, Object... params) {
		return String.format("BEGIN TREES;\n\tTree result = %s\nEND;", super.format(entities));
	}

}
