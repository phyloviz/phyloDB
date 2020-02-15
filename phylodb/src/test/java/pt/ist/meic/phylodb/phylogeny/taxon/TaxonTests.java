package pt.ist.meic.phylodb.phylogeny.taxon;

import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class TaxonTests {

	@Autowired
	protected Session session;

	protected static final String[] IDS = {"Brucella spp.", "Escherichia/Shigella", "Salmonella", "Salonella typhi", "Streptococcus pneumoniae", "teste"};


	protected void arrangeWithRelationships(String id) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("id", id);
			put("description", null);
		}};
		session.query("CREATE (:Taxon {_id: $id, description: $description})-[:TEST]->(:teste)", params);
	}

	protected void arrange(String... ids) {
		for (String id : ids) {
			Map<String, Object> params = new HashMap<String, Object>() {{
				put("id", id);
				put("description", null);
			}};
			session.query("CREATE (:Taxon {_id: $id, description: $description})", params);
		}
	}

}
