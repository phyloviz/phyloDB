package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Repository {

	protected static final Logger LOG = LoggerFactory.getLogger(Repository.class);
	private final Session session;

	protected Repository(Session session) {
		this.session = session;
	}

	protected final <T> T query(Class<T> _class, Query query) {
		LOG.info("\nQuery: " + query.getExpression() + "\nParameters: " + query.getParameters().toString());
		return session.queryForObject(_class, query.getExpression(), query.getParameters());
	}

	protected final Result query(Query query) {
		LOG.info("\nQuery: " + query.getExpression() + "\nParameters: " + query.getParameters().toString());
		return session.query(query.getExpression(), query.getParameters());
	}

	protected final void execute(Query query) {
		LOG.info("\nQuery: " + query.getExpression() + "\nParameters: " + query.getParameters().toString());
		session.query(query.getExpression(), query.getParameters());
		session.clear();
	}

}
