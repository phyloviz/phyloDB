package pt.ist.meic.phylodb.typing.ancillary.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class AncillaryRepository {

	private Session session;

	public AncillaryRepository(Session session) {
		this.session = session;
	}
}
