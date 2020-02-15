package pt.ist.meic.phylodb.phylogeny.locus;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;

import java.util.Collections;
import java.util.List;

@Repository
public class LocusRepository {

	private Session session;

	public LocusRepository(Session session) {
		this.session = session;
	}

	public List<Locus> findAll(String key) {
		return Collections.emptyList();
	}
}
