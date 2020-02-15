package pt.ist.meic.phylodb.typing.isolate.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class IsolateRepository {

	private Session session;

	public IsolateRepository(Session session) {
		this.session = session;
	}
}
