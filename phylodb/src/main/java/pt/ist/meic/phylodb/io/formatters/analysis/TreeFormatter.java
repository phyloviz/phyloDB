package pt.ist.meic.phylodb.io.formatters.analysis;

import pt.ist.meic.phylodb.analysis.inference.model.Edge;
import pt.ist.meic.phylodb.io.formatters.Formatter;

import java.util.HashMap;

/**
 * Base class for newick and nexus formatters
 */
public abstract class TreeFormatter extends Formatter<Edge> {

	public static final String NEWICK = "newick", NEXUS = "nexus";

	/**
	 * Retrieves the requested TreeFormatter
	 *
	 * @param format a String that identifies the formatter
	 * @return the TreeFormatter represented by the parameter format
	 */
	public static TreeFormatter get(String format) {
		return new HashMap<String, TreeFormatter>() {{
			put(NEWICK, new NewickFormatter());
			put(NEXUS, new NexusFormatter());
		}}.get(format);
	}

}
