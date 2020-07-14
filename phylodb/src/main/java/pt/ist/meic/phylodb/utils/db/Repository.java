package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;

public abstract class Repository {

	private final Session session;

	protected Repository(Session session) {
		this.session = session;
	}

	protected final <T> T query(Class<T> _class, Query query) {
		return session.queryForObject(_class, query.getExpression(), query.getParameters());
	}

	protected final Result query(Query query) {
		return session.query(query.getExpression(), query.getParameters());
	}

	public final Result execute(Query query) {
		Result result = session.query(query.getExpression(), query.getParameters());
		session.clear();
		return result;
	}

}
