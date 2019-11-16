package pt.ist.meic.phylodb.locus.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class LocusRepositoryImpl implements LocusCypherRepository {

	private Session session;

	public LocusRepositoryImpl(Session session) {
		this.session = session;
	}
}
