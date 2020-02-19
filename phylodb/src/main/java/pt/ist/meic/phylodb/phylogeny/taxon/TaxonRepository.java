package pt.ist.meic.phylodb.phylogeny.taxon;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.EntityRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TaxonRepository extends EntityRepository<Taxon, String> {

	public static final String GET_ALL = "(t:Taxon)";
	public static final String GET = "(t:Taxon {id: $taxonKey})";
	public static final String POST = "(t:Taxon {id: $taxonKey, description: $description})";
	public static final String PUT = "t.description = $description";

	public TaxonRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Taxon> getAll(Map<String, Object> params, Object... filters) {
		return queryAll(Taxon.class, MATCH + PAGE, params, GET_ALL, "t", "t.id", filters);
	}

	@Override
	protected Taxon get(String key) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", key);
		}};
		return query(Taxon.class, MATCH, params, GET, "t");
	}

	@Override
	protected boolean exists(Taxon taxon) {
		return get(taxon.getId()) != null;
	}

	@Override
	protected void create(Taxon taxon) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", taxon.getId());
			put("description", taxon.getDescription());
		}};
		execute(CREATE, params, POST);
	}

	@Override
	protected void update(Taxon taxon) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", taxon.getId());
			put("description", taxon.getDescription());
		}};
		execute(UPDATE, params, GET, PUT);

	}

	@Override
	protected void delete(String key) {
		HashMap<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", key);
		}};
		execute(REMOVE, params, GET, "t");
	}

}
