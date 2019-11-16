package pt.ist.meic.phylodb.allele.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class AlleleRepositoryImpl implements AlleleCypherRepository{

	private Session session;

	public AlleleRepositoryImpl(Session session) {
		this.session = session;
	}
}
