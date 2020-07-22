package pt.ist.meic.phylodb.io.formatters.analysis;

import pt.ist.meic.phylodb.analysis.inference.model.Edge;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.VersionedRepository.CURRENT_VERSION_VALUE;

/**
 * NewickFormatter is the implementation of the formatter to parse and format inferences in newick
 */
public class NewickFormatter extends TreeFormatter {

	private String projectId;
	private String datasetId;

	@Override
	protected boolean init(Iterator<String> it, Object... params) {
		this.projectId = (String) params[0];
		this.datasetId = (String) params[1];
		return true;
	}

	@Override
	protected boolean parse(String line, boolean last, Consumer<Edge> add) {
		StringBuilder newick = new StringBuilder(line.trim());
		List<Edge> aux = new ArrayList<>();
		Stack<List<Edge>> levels = new Stack<>();
		while (newick.length() > 0) {
			switch (newick.charAt(0)) {
				case ';':
					if (!levels.isEmpty() || aux.isEmpty())
						return false;
					newick.delete(0, 1);
					break;
				case ',':
					newick.delete(0, 1);
					break;
				case '(':
					levels.push(new ArrayList<>());
					newick.delete(0, 1);
					break;
				case ')':
					newick.delete(0, 1);
					List<Edge> children = new ArrayList<>(levels.pop());
					String parentId = parseNode(newick, levels, aux);
					if (parentId == null)
						return false;
					for (Edge child : children) {
						if (parentId.equals(child.getTo().getPrimaryKey().getId()))
							return false;
						VersionedEntity<Profile.PrimaryKey> from = new VersionedEntity<>(new Profile.PrimaryKey(projectId, datasetId, parentId), CURRENT_VERSION_VALUE, false);
						Edge edge = new Edge(from, child.getTo(), child.getWeight());
						add.accept(edge);
						aux.add(edge);
					}
					break;
				default:
					if (parseNode(newick, levels, aux) == null)
						return false;
			}
		}
		return levels.isEmpty();
	}

	@Override
	public String format(List<Edge> entities, Object... params) {
		StringBuilder data = new StringBuilder();
		List<VersionedEntity<Profile.PrimaryKey>> roots = entities.stream()
				.map(Edge::getFrom)
				.filter(p -> entities.stream().noneMatch(e -> e.getTo().equals(p)))
				.distinct()
				.collect(Collectors.toList());
		List<Edge> edges = new ArrayList<>(entities);
		for (VersionedEntity<Profile.PrimaryKey> root : roots) {
			format(edges, root, data);
			data.append(root.getPrimaryKey().getId()).append(';');
		}
		return data.toString();
	}

	private String parseNode(StringBuilder newick, Stack<List<Edge>> levels, List<Edge> aux) {
		if (!newick.toString().matches(".*[),;].*"))
			return null;
		String info = newick.toString().split("[),;]", 2)[0];
		newick.delete(0, info.length());
		String[] values = info.split(":", -1);
		if (values.length > 1) {
			if (newick.indexOf(";") == 0 || values.length > 2 || !values[1].matches("[\\d]*") || values[0].isEmpty())
				return null;
			VersionedEntity<Profile.PrimaryKey> to = new VersionedEntity<>(new Profile.PrimaryKey(projectId, datasetId, values[0]), CURRENT_VERSION_VALUE, false);
			Edge edge = new Edge(null, to, Long.parseLong(values[1]));
			levels.peek().add(edge);
			aux.add(edge);
			return values[0];
		} else if (newick.indexOf(";") != 0) {
			return null;
		}
		return values[0];
	}

	private void format(List<Edge> entities, VersionedEntity<Profile.PrimaryKey> root, StringBuilder data) {
		List<Edge> edges = entities.stream()
				.filter(e -> e.getFrom().equals(root) || e.getTo().equals(root))
				.collect(Collectors.toList());
		entities.removeAll(edges);
		if (!edges.isEmpty()) {
			data.append('(');
			for (Edge edge : edges) {
				format(entities, edge.getTo(), data);
				data.append(edge.getTo().getPrimaryKey().getId()).append(':').append(edge.getWeight()).append(',');
			}
			data.replace(data.length() - 1, data.length(), ")");
		}
	}

}
