package pt.ist.meic.phylodb.utils.db;

import java.util.*;

public class Query {

	private static final String PLACEHOLDER = "$";

	private StringBuilder query;
	private List<Object> parameters;

	public Query(String query, Object... parameters) {
		this.query = new StringBuilder(query);
		this.parameters = new ArrayList<>(Arrays.asList(parameters));
	}

	public void addParameter(Object... parameters) {
		this.parameters.addAll(Arrays.asList(parameters));
	}

	public Query appendQuery(String query, Object... params) {
		this.query.append(String.format(query, params));
		return this;
	}

	public Query subQuery(int index) {
		query.delete(index, query.length() - 1);
		return this;
	}

	public int length() {
		return query.length();
	}

	public String getExpression() {
		String[] parts = query.toString().split("\\$", -1);
		StringBuilder parameterized = new StringBuilder(parts[0]);
		for (int i = 1; i < parts.length; i++)
			parameterized.append(PLACEHOLDER).append(i).append(parts[i]);
		return parameterized.toString() + ";";
	}

	public Map<String, Object> getParameters() {
		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < parameters.size(); i++)
			params.put(String.valueOf(i + 1), parameters.get(i));
		return params;
	}

}
