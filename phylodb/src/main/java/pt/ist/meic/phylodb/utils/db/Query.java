package pt.ist.meic.phylodb.utils.db;

import java.util.*;

/**
 * A query is used to build and hold queries to execute upon a database
 */
public class Query {

	private static final String PLACEHOLDER = "$";

	private final StringBuilder query;
	private final List<Object> parameters;

	public Query(String query, Object... parameters) {
		this.query = new StringBuilder(query);
		this.parameters = new ArrayList<>(Arrays.asList(parameters));
	}

	/**
	 * Add parameters to the query
	 *
	 * @param parameters parameters to be added to the query
	 */
	public void addParameter(Object... parameters) {
		this.parameters.addAll(Arrays.asList(parameters));
	}

	/**
	 * Add a statement and parameters to the query
	 *
	 * @param query  statement to be added
	 * @param params parameters to be added
	 * @return the query
	 */
	public Query appendQuery(String query, Object... params) {
		this.query.append(String.format(query, params));
		return this;
	}

	/**
	 * Removes statements from the query
	 *
	 * @param index begin index to sub query
	 * @return the query
	 */
	public Query subQuery(int index) {
		query.delete(index, query.length() - 1);
		return this;
	}

	/**
	 * Retrieves the length of the query
	 *
	 * @return the length of the query
	 */
	public int length() {
		return query.length();
	}

	/**
	 * Retrieves the set of statements of the query as a parameterized expression
	 *
	 * @return parameterized expression
	 */
	public String getExpression() {
		String[] parts = query.toString().split("\\$", -1);
		StringBuilder parameterized = new StringBuilder(parts[0]);
		for (int i = 1; i < parts.length; i++)
			parameterized.append(PLACEHOLDER).append(i).append(parts[i]);
		return parameterized.toString() + ";";
	}

	/**
	 * A map of ids and parameter which indicates to which query parameter, a parameter belongs to
	 *
	 * @return map of parameters
	 */
	public Map<String, Object> getParameters() {
		Map<String, Object> params = new HashMap<>();
		for (int i = 0; i < parameters.size(); i++)
			params.put(String.valueOf(i + 1), parameters.get(i));
		return params;
	}

}
