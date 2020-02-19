package pt.ist.meic.phylodb.typing.schema.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class SchemaRepository {

	private Session session;

	public SchemaRepository(Session session) {
		this.session = session;
	}

}
