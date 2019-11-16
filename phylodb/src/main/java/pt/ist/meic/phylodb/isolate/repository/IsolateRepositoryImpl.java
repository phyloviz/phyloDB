package pt.ist.meic.phylodb.isolate.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class IsolateRepositoryImpl implements IsolateCypherRepository {

	private Session session;

	public IsolateRepositoryImpl(Session session) {
		this.session = session;
	}
}
