package pt.ist.meic.phylodb.analysis.visualization;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;

@Repository
public class VisualizationRepository {

	private Session session;

	public VisualizationRepository(Session session) {
		this.session = session;
	}

}
