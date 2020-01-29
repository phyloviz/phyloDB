package pt.ist.meic.phylodb.phylogeny.taxon.repository;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class TaxonRepositoryImpl implements TaxonCypherRepository {

	private Session session;

	public TaxonRepositoryImpl(Session session) {
		this.session = session;
	}
}
