package pt.ist.meic.phylodb.analysis.visualization.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class CoordinateRepository {

	private Session session;

	public CoordinateRepository(Session session) {
		this.session = session;
	}

}
