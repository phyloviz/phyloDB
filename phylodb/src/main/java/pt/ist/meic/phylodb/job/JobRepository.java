package pt.ist.meic.phylodb.job;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class JobRepository {

	private Session session;

	public JobRepository(Session session) {
		this.session = session;
	}

}
