package pt.ist.meic.phylodb.analysis.inference.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class DistanceRepository {

	private Session session;

	public DistanceRepository(Session session) {
		this.session = session;
	}

}
