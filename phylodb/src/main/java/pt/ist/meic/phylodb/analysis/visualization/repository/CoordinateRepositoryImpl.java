package pt.ist.meic.phylodb.analysis.visualization.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class CoordinateRepositoryImpl implements CoordinateCypherRepository {

	private Session session;

	public CoordinateRepositoryImpl(Session session) {
		this.session = session;
	}
}
