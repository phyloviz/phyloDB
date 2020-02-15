package pt.ist.meic.phylodb.authentication.user.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

	private Session session;

	public UserRepository(Session session) {
		this.session = session;
	}
}
