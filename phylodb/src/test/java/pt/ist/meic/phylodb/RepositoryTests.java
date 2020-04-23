package pt.ist.meic.phylodb;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.utils.db.Query;

@Transactional
public abstract class RepositoryTests  extends Test{

	@Autowired
	private Session session;

	protected Result query(Query query) {
		return session.query(query.getExpression(), query.getParameters());
	}

	protected final <T> T query(Class<T> _class, Query query) {
		return session.queryForObject(_class, query.getExpression(), query.getParameters());
	}

	protected void execute(Query query) {
		System.out.println("\nQuery: " + query.getExpression() + "\nParameters: " + query.getParameters().toString());
		session.query(query.getExpression(), query.getParameters());
		session.clear();
	}
}
