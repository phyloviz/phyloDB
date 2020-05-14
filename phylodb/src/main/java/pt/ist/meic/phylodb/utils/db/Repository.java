package pt.ist.meic.phylodb.utils.db;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class Repository<E, K> {

	protected static final Logger LOG = LoggerFactory.getLogger(Repository.class);
	private final Session session;

	protected Repository(Session session) {
		this.session = session;
	}

	protected abstract Result getAll(int page, int limit, Object... filters);

	protected abstract boolean isPresent(K key);

	protected abstract void store(E entity);

	protected abstract void delete(K key);

	protected abstract E parse(Map<String, Object> row);

	protected final <T> T query(Class<T> _class, Query query) {
		LOG.info("\nQuery: " + query.getExpression() + "\nParameters: " + query.getParameters().toString());
		return session.queryForObject(_class, query.getExpression(), query.getParameters());
	}

	protected final Result query(Query query) {
		LOG.info("\nQuery: " + query.getExpression() + "\nParameters: " + query.getParameters().toString());
		return session.query(query.getExpression(), query.getParameters());
	}

	protected final Result execute(Query query) {
		LOG.info("\nQuery: " + query.getExpression() + "\nParameters: " + query.getParameters().toString());
		Result result = session.query(query.getExpression(), query.getParameters());
		session.clear();
		return result;
	}

	public Optional<List<E>> findAll(int page, int limit, Object... filters) {
		if (page < 0 || limit < 0) return Optional.empty();
		Result result = getAll(page * limit, limit, filters);
		if (result == null) return Optional.empty();
		return Optional.of(StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.collect(Collectors.toList()));
	}

	public boolean exists(K key) {
		return key != null && isPresent(key);
	}

	public boolean save(E entity) {
		if (entity == null)
			return false;
		store(entity);
		return true;
	}

	public boolean remove(K key) {
		if (!exists(key))
			return false;
		delete(key);
		return true;
	}

}
