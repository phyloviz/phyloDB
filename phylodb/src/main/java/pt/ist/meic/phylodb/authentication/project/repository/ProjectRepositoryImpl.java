package pt.ist.meic.phylodb.authentication.project.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepositoryImpl implements ProjectCypherRepository {

	private Session session;

	public ProjectRepositoryImpl(Session session) {
		this.session = session;
	}
}
