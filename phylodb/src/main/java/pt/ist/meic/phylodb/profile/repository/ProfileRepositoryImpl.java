package pt.ist.meic.phylodb.profile.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileRepositoryImpl implements ProfileCypherRepository {

	private Session session;

	public ProfileRepositoryImpl(Session session) {
		this.session = session;
	}
}
