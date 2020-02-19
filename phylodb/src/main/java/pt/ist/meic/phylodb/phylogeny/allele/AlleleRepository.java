package pt.ist.meic.phylodb.phylogeny.allele;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.formatters.datasets.Dataset;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.utils.EntityRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AlleleRepository extends EntityRepository<Allele, Allele.PrimaryKey> {

	public static final String PATH = "(t:Taxon {id: $taxonKey})-[:CONTAINS]->(l:Locus {id: $locusKey})-[CONTAINS]->";
	public static final String GET_ALL = PATH + "(a:Allele)";
	public static final String GET = PATH + "(a:Allele {id: $alleleKey})";
	public static final String POST = "(a:Allele {id: $alleleKey, sequence: $sequence})";
	public static final String PUT = "a.sequence = $sequence";

	public AlleleRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Allele> getAll(Map<String, Object> params, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		params.put("taxonKey", filters[0]);
		params.put("locusKey", filters[1]);
		return queryAll(Allele.class, MATCH + PAGE, params, GET_ALL, "a", "a.id");
	}

	@Override
	protected Allele get(Allele.PrimaryKey key) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", key.getTaxonId());
			put("locusKey", key.getLocusId());
			put("alleleKey", key.getId());
		}};
		return query(Allele.class, MATCH, params, GET, "a");
	}

	@Override
	protected boolean exists(Allele allele) {
		return get(allele.getPrimaryKey()) != null;
	}

	@Override
	protected void create(Allele allele) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", allele.getTaxonId());
			put("locusKey", allele.getLocusId());
			put("alleleKey", allele.getId());
			put("sequence", allele.getSequence());
		}};
		execute(MATCH_AND_RELATE, params, LocusRepository.GET, "(l)-[:CONTAINS]->" + POST);
	}

	@Override
	protected void update(Allele allele) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", allele.getTaxonId());
			put("locusKey", allele.getLocusId());
			put("alleleKey", allele.getId());
			put("sequence", allele.getSequence());
		}};
		execute(UPDATE, params, GET, PUT);
	}

	@Override
	protected void delete(Allele.PrimaryKey key) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", key.getTaxonId());
			put("locusKey", key.getLocusId());
			put("alleleKey", key.getId());
		}};
		execute(REMOVE, params, GET, "a");
	}

	public void saveAll(String taxon, String locus, Dataset<Allele> dataset) {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("taxonKey", taxon);
			put("locusKey", locus);
		}};
		StringBuilder createAlleles = new StringBuilder();
		List<Allele> alleles = dataset.getEntities();
		for (int i = 0; i < alleles.size(); i++) {
			createAlleles.append("(l)-[:CONTAINS]->(:Allele {id: $alleleKey").append(i)
					.append(", sequence: $sequence").append(i).append("}),");
			params.put("alleleKey" + i, alleles.get(i).getId());
			params.put("sequence" + i, alleles.get(i).getSequence());
		}
		String parts = createAlleles.deleteCharAt(createAlleles.length() - 1).toString();
		execute(MATCH_AND_RELATE, params, LocusRepository.GET, parts);
	}

}
