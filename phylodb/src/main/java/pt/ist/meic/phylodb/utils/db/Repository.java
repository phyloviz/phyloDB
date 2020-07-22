package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;

/**
 * A Repository allows to perform queries to a database through a {@link Session}
 */
public abstract class Repository {

	private final Session session;

	protected Repository(Session session) {
		this.session = session;
	}

	/**
	 * Performs a read query to the database which returns an object of type <code>_class<code/>
	 *
	 * @param _class type of the object to return
	 * @param query  query to execute
	 * @param <T>    type of the object to return
	 * @return an object instance of type {@link T}
	 */
	protected final <T> T query(Class<T> _class, Query query) {
		return session.queryForObject(_class, query.getExpression(), query.getParameters());
	}

	/**
	 * Performs a read query to the database and returns a {@link Result}
	 *
	 * @param query query to be executed
	 * @return a {@link Result} with the result of the query
	 */
	protected final Result query(Query query) {
		return session.query(query.getExpression(), query.getParameters());
	}

	/**
	 * Performs an update query to the database and returns a {@link Result}, and clears the session so the changes can be seen immediately
	 *
	 * @param query query to be executed
	 * @return a {@link Result} with the result of the query
	 */
	public final Result execute(Query query) {
		Result result = session.query(query.getExpression(), query.getParameters());
		session.clear();
		return result;
	}

}
