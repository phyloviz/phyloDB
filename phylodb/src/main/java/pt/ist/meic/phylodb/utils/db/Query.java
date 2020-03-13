package pt.ist.meic.phylodb.utils.db;

import java.util.*;

public class Query {

	private StringBuilder query;
	private List<Object> parameters;

	public Query(String query, Object... parameters) {
		this.query = new StringBuilder(query);
		this.parameters = new ArrayList<>(Arrays.asList(parameters));
	}

	public Query addParameter(Object... parameters) {
		this.parameters.addAll(Arrays.asList(parameters));
		return this;
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
		return query.toString();
	}

	public Map<String, Object> getParameters() {
		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < parameters.size(); i++)
			params.put(String.valueOf(i), parameters.get(i));
		return params;
	}

}
