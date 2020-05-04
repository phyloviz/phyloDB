package pt.ist.meic.phylodb;

import org.neo4j.ogm.model.QueryStatistics;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.utils.db.Query;

@Transactional
public abstract class RepositoryTests  extends Test {

	@Autowired
	private Session session;

	protected Integer count(){
		return query(Integer.class, new Query("MATCH (n) return COUNT(n)"));
	}
	protected void xpto(){
		Result r = query(new Query("MATCH (n) return n.id"));
		while(r.iterator().hasNext()) {
			Object x = r.iterator().next();
			int i = 1;
		}
	}

	protected Result query(Query query) {
		System.out.println("\nQuery: " + query.getExpression() + "\nParameters: " + query.getParameters().toString());
		return session.query(query.getExpression(), query.getParameters());
	}

	protected final <T> T query(Class<T> _class, Query query) {
		return session.queryForObject(_class, query.getExpression(), query.getParameters());
	}

	protected QueryStatistics execute(Query query) {
		System.out.println("\nQuery: " + query.getExpression() + "\nParameters: " + query.getParameters().toString());
		QueryStatistics statistics = session.query(query.getExpression(), query.getParameters()).queryStatistics();
		session.clear();
		return statistics;
	}
}
