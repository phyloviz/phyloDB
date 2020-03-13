package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class Repository {

	protected static final Logger LOG = LoggerFactory.getLogger(Repository.class);
	private final Session session;

	protected Repository(Session session) {
		this.session = session;
	}

	protected final <T> List<T> queryAll(Class<T> _class, Query query) {
		LOG.info("\nQuery: " + query.getExpression() + "\nParameters: " + query.getParameters().toString());
		return StreamSupport.stream(session.query(_class, query.getExpression(), query.getParameters()).spliterator(), false)
				.collect(Collectors.toList());
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
