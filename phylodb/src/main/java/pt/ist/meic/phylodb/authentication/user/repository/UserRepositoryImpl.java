package pt.ist.meic.phylodb.authentication.user.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserCypherRepository {

	private Session session;

	public UserRepositoryImpl(Session session) {
		this.session = session;
	}
}
