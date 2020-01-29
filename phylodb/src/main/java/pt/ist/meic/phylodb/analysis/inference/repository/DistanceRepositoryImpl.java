package pt.ist.meic.phylodb.analysis.inference.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class DistanceRepositoryImpl implements DistanceCypherRepository {

	private Session session;

	public DistanceRepositoryImpl(Session session) {
		this.session = session;
	}
}
