package pt.ist.meic.phylodb.typing.profile.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileRepository {

	private Session session;

	public ProfileRepository(Session session) {
		this.session = session;
	}
}
