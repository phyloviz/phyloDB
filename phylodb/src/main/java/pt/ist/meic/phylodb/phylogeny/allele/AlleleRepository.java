package pt.ist.meic.phylodb.phylogeny.allele;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class AlleleRepository {

	private Session session;

	public AlleleRepository(Session session) {
		this.session = session;
	}
}
