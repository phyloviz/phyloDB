package pt.ist.meic.phylodb.typing.dataset.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class DatasetRepository {

	private Session session;

	public DatasetRepository(Session session) {
		this.session = session;
	}

}
