package pt.ist.meic.phylodb.ancillary.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class AncillaryRepositoryImpl implements AncillaryCypherRepository {

	private Session session;

	public AncillaryRepositoryImpl(Session session) {
		this.session = session;
	}
}
