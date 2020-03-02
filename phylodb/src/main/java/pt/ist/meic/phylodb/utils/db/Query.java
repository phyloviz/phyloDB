package pt.ist.meic.phylodb.utils.db;

import java.util.*;

public class Query {

	private static final String MATCH = "MATCH %s", RETURN = "RETURN %s", WITH = "WITH %s",
			CREATE = "CREATE %s", UPDATE = MATCH + "SET %s", REMOVE =  MATCH + "DETACH DELETE %s",
			PAGE = "ORDER BY %s SKIP $page LIMIT $limit";

	private StringBuilder query;
	private List<String> parts;
	private Map<String, Object> params;
	private int size = 0;

	public Query() {
		this.query = new StringBuilder();
		this.parts = new ArrayList<>();
		this.params = new HashMap<>();
	}

	public Query match(String expression) {
		return compose(MATCH, expression);
	}

	public Query retrieve(String expression) {
		return compose(RETURN, expression);
	}

	public Query with(String expression) {
		return compose(WITH, expression);
	}

	public Query create(String expression) {
		return compose(CREATE, expression);
	}

	public Query page(String expression) {
		return compose(PAGE, expression);
	}

	public Query update(String match, String update) {
		return compose(UPDATE, match, update);
	}

	public Query remove(String match, String variable) {
		return compose(REMOVE, match, variable);
	}

	public Query parameters(Object... params) {
		int length = params.length;
		if(query.indexOf(PAGE) != -1) {
			this.params.put("page", params[length - 2]);
			this.params.put("limit", params[length - 1]);
			length -= 2;
		}
		for (int i = 0; i < length; i++)
			this.params.put(String.valueOf(size + i), params[i]);
		size += length;
		return this;
	}

	public String getExpression() {
		return String.format(query.toString(), parts.toArray());
	}

	public Map<String, Object> getParameters() {
		return params;
	}

	private Query compose(String operation, String... expression) {
		query.append(" ").append(operation);
		parts.addAll(Arrays.asList(expression));
		return this;
	}


}
