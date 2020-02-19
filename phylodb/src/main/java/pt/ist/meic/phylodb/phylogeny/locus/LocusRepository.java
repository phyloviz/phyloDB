package pt.ist.meic.phylodb.phylogeny.locus;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.utils.EntityRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class LocusRepository extends EntityRepository<Locus, Locus.PrimaryKey> {

	public static final String PATH = "(t:Taxon {id: $taxonKey})-[:CONTAINS]->";
	public static final String GET_ALL = PATH + "(l:Locus)";
	public static final String GET = PATH + "(l:Locus {id: $locusKey})";
	public static final String POST = "(l:Locus {id: $locusKey, description: $description})";
	public static final String PUT = "l.description = $description";

	public LocusRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Locus> getAll(Map<String, Object> params, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		params.put("locusKey", filters[0]);
		return queryAll(Locus.class, MATCH + PAGE, params, GET_ALL, "l", "l.id");
	}

	@Override
	protected Locus get(Locus.PrimaryKey key) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", key.getTaxonId());
			put("locusKey", key.getId());
		}};
		return query(Locus.class, MATCH, params, GET, "l");
	}

	@Override
	protected boolean exists(Locus locus) {
		return get(locus.getPrimaryKey()) != null;
	}

	@Override
	protected void create(Locus locus) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", locus.getTaxonId());
			put("locusKey", locus.getId());
			put("description", locus.getDescription());
		}};
		execute(MATCH_AND_RELATE, params, TaxonRepository.GET, "(t)-[:CONTAINS]->" + POST);
	}

	@Override
	protected void update(Locus locus) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", locus.getTaxonId());
			put("locusKey", locus.getId());
			put("description", locus.getDescription());
		}};
		execute(UPDATE, params, GET, PUT);
	}

	@Override
	protected void delete(Locus.PrimaryKey key) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", key.getTaxonId());
			put("locusKey", key.getId());
		}};
		execute(REMOVE, params, GET, "l");
	}

}
