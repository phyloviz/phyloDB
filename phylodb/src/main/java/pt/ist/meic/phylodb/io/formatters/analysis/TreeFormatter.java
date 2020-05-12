package pt.ist.meic.phylodb.io.formatters.analysis;

import pt.ist.meic.phylodb.analysis.inference.model.Edge;
import pt.ist.meic.phylodb.io.formatters.Formatter;

import java.util.HashMap;

public abstract class TreeFormatter extends Formatter<Edge> {

	public static final String NEWICK = "newick", NEXUS = "nexus";

	public static TreeFormatter get(String format) {
		return new HashMap<String, TreeFormatter>() {{
			put(NEWICK, new NewickFormatter());
			put(NEXUS, new NexusFormatter());
		}}.get(format);
	}

}
