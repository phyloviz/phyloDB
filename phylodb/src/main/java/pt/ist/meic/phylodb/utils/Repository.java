package pt.ist.meic.phylodb.utils;

import org.neo4j.ogm.session.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class Repository {

	public static final String MATCH = "MATCH %s RETURN %s";
	public static final String CREATE = "CREATE %s";
	public static final String MATCH_AND_RELATE = "MATCH %s " + CREATE;
	public static final String UPDATE = "MATCH %s SET %s";
	public static final String REMOVE = "MATCH %s DETACH DELETE %s";
	public static final String PAGE = " ORDER BY %s SKIP $page LIMIT $limit";

	private final Session session;

	protected Repository(Session session) {
		this.session = session;
	}

	protected final <T> List<T> queryAll(Class<T> _class, String query, Map<String, Object> params, Object... parts) {
		return StreamSupport.stream(session.query(_class, String.format(query + ';', parts), params).spliterator(), false)
				.collect(Collectors.toList());
	}

	protected final <T> List<T> queryAll(Class<T> _class, String query) {
		return queryAll(_class, query, new HashMap<>());
	}

	protected final <T> T query(Class<T> _class, String query, Map<String, Object> params, Object... parts) {
		return session.queryForObject(_class, String.format(query + ';', parts), params);
	}

	protected final void execute(String query, Map<String, Object> params, Object... parts) {
		session.query(String.format(query + ';', parts), params);
		session.clear();
	}

}
