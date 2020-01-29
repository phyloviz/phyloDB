package pt.ist.meic.phylodb.typing.dataset.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class DatasetRepositoryImpl implements DatasetCypherRepository {

	private Session session;

	public DatasetRepositoryImpl(Session session) {
		this.session = session;
	}
}
