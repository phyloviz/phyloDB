package pt.ist.meic.phylodb.job.repository;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class JobRepositoryImpl implements JobCypherRepository {

	private Session session;

	public JobRepositoryImpl(Session session) {
		this.session = session;
	}

}
