package pt.ist.meic.phylodb.io.formatters.analysis;

import pt.ist.meic.phylodb.analysis.inference.model.Edge;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class NewickFormatter extends TreeFormatter {

	private String projectId;
	private String datasetId;
	protected String missing;

	@Override
	protected boolean init(Iterator<String> it, Object... params) {
		this.projectId = (String) params[0];
		this.datasetId = (String) params[1];
		return true;
	}

	@Override
	protected boolean parse(String line, boolean last, Consumer<Edge> add) {
		StringBuilder newick = new StringBuilder(line.trim());
		Stack<List<Edge>> levels = new Stack<>();
		while (newick.length() > 0) {
			switch (newick.charAt(0)) {
				case ';':
					if (!levels.isEmpty())
						return false;
					newick.delete(0, 1);
					break;
				case ',': newick.delete(0, 1);
					break;
				case '(': levels.push(new ArrayList<>());
					newick.delete(0, 1);
					break;
				case ')':
					newick.delete(0, 1);
					List<Edge> children = new ArrayList<>(levels.pop());
					String parentId = parseNode(newick, levels);
					if(parentId == null)
						return false;
					for (Edge child :children) {
						if(parentId.equals(child.getTo().getPrimaryKey().getId()))
							return false;
						Entity<Profile.PrimaryKey> from = new Entity<>(new Profile.PrimaryKey(projectId, datasetId, parentId), CURRENT_VERSION_VALUE, false);
						add.accept(new Edge(from, child.getTo(), child.getWeight()));
					}
					break;
				default:
					if(parseNode(newick, levels) == null)
						return false;
			}
		}
		return levels.isEmpty();
	}

	@Override
	public String format(List<Edge> entities, Object... params) {
		StringBuilder data = new StringBuilder();
		List<Entity<Profile.PrimaryKey>> roots = entities.stream()
				.map(Edge::getFrom)
				.filter(p -> entities.stream().noneMatch(e -> e.getTo().equals(p)))
				.distinct()
				.collect(Collectors.toList());
		List<Edge> edges = new ArrayList<>(entities);
		for (Entity<Profile.PrimaryKey> root : roots) {
			format(edges, root, data);
			data.append(root.getPrimaryKey().getId()).append(';');
		}
		return data.toString();
	}

	private String parseNode(StringBuilder newick, Stack<List<Edge>> levels) {
		String info = newick.toString().split("[),;]", 2)[0];
		newick.delete(0, info.length());
		String[] values = info.split(":", 2);
		if (newick.length() > 0 && newick.charAt(0) != ';') {
			if (values.length != 2 || Arrays.stream(values).anyMatch(s -> s.matches(String.format("[\\s%s]*", missing)) || s.isEmpty()) ||
					!values[1].matches("[\\d]*"))
				return null;
			Entity<Profile.PrimaryKey> to = new Entity<>(new Profile.PrimaryKey(projectId, datasetId, values[0]), CURRENT_VERSION_VALUE, false);
			levels.peek().add(new Edge(null, to, Integer.parseInt(values[1])));
			return values[0];
		}
		return values[0];
	}

	private void format(List<Edge> entities, Entity<Profile.PrimaryKey> root, StringBuilder data) {
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
