package pt.ist.meic.phylodb.phylogeny.locus;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class LocusRepository extends EntityRepository<Locus, Locus.PrimaryKey> {

	public LocusRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Locus> getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus)\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to)\n" +
				"RETURN l SKIP $1 LIMIT $2";
		return queryAll(Locus.class, new Query(statement, filters[0], page, limit));
	}

	@Override
	protected Locus get(Locus.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus {id: $1})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to)\n" +
				"RETURN l";
		return query(Locus.class, new Query(statement, key.getTaxonId(), key.getId()));
	}

	@Override
	protected boolean exists(Locus locus) {
		return get(locus.getPrimaryKey()) != null;
	}

	@Override
	protected void create(Locus locus) {
		String statement = "MATCH (t:Taxon {id: $0})\n" +
				"WHERE NOT EXISTS(t.to)\n" +
				"CREATE (t)-[:CONTAINS]->(l:Locus {id: $1, description: $2, from: datetime()})";
		execute(new Query(statement, locus.getTaxonId(), locus.getId(), locus.getDescription()));
	}

	@Override
	protected void update(Locus locus) {
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus {id: $1})\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to)\n" +
				"WITH l\n" +
				"CALL apoc.refactor.cloneNodes([l], true) YIELD input, output\n" +
				"SET l.description = $2, l.from = datetime(), output.to = datetime()";
		execute(new Query(statement, locus.getTaxonId(), locus.getId(), locus.getDescription()));
	}

	@Override
	protected void delete(Locus.PrimaryKey key) {
		String statement = "MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus {id: $1})\n" +
				"WHERE NOT EXISTS(t.to) AND WHERE NOT EXISTS(l.to) SET l.to = datetime() WITH l\n" +
				"MATCH (l)-[:CONTAINS]->(a:Allele) WHERE NOT EXISTS(a.to) SET a.to = datetime()";
		execute(new Query(statement, key.getTaxonId(), key.getId()));
	}


	public boolean existsAll(String taxonId, String[] lociIds) {
		String parameterized = IntStream.range(0, lociIds.length)
				.mapToObj(i -> "$" + (i + 1))
				.collect(Collectors.joining(","));
		String statement = String.format("MATCH (t:Taxon {id: $0})-[:CONTAINS]->(l:Locus)\n" +
				"WHERE NOT EXISTS(t.to) AND NOT EXISTS(l.to) AND l.id IN [%s]\n" +
				"RETURN COUNT(l.id)", parameterized);
		List<String> params = new ArrayList<>();
		params.add(taxonId);
		params.addAll(Arrays.asList(lociIds));
		return query(Integer.class, new Query(statement, params.toArray())) == lociIds.length;
	}

}
