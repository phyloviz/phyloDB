package pt.ist.meic.phylodb.phylogeny.taxon;

import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class TaxonTests {

	protected static final String[] IDS = {"Brucella spp.", "Escherichia/Shigella", "Salmonella", "Salmonella typhi", "Streptococcus pneumoniae", "teste"};
	@Autowired
	protected Session session;

	protected void arrangeWithRelationships(String id) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("id", id);
			put("description", null);
		}};
		session.query("CREATE (:Taxon {id: $id, description: $description})-[:CONTAINS]->(:Locus)", params);
	}

	protected void arrange(String... ids) {
		for (String id : ids) {
			Map<String, Object> params = new HashMap<String, Object>() {{
				put("id", id);
				put("description", null);
			}};
			session.query("CREATE (:Taxon {id: $id, description: $description})", params);
		}
	}

}
