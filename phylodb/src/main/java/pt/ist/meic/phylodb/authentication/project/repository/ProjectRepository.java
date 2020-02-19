package pt.ist.meic.phylodb.authentication.project.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepository {

	private Session session;

	public ProjectRepository(Session session) {
		this.session = session;
	}

}
