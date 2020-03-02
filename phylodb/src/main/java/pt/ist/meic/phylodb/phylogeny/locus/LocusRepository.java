package pt.ist.meic.phylodb.phylogeny.locus;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.List;

@Repository
public class LocusRepository extends EntityRepository<Locus, Locus.PrimaryKey> {

	private static final String RELATION = "-[:CONTAINS]->", PATH = TaxonRepository.GET + RELATION;
	public static final String VARIABLE = "l", LABEL = "Locus", ORDER = String.format("%s.id", VARIABLE),
			GET_ALL = PATH + String.format("(%s:%s)", VARIABLE, LABEL),
			GET = PATH + String.format("(%s:%s {id: $1})", VARIABLE, LABEL),
			POST = String.format("(:%s {id: $1, description: $2})", LABEL),
			PUT = String.format("%s.description = $2", VARIABLE);


	public LocusRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Locus> getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		Query query = new Query().match(GET_ALL)
				.retrieve(VARIABLE)
				.page(ORDER)
				.parameters(filters[0], page, limit);
		return queryAll(Locus.class, query);
	}

	@Override
	protected Locus get(Locus.PrimaryKey key) {
		Query query = new Query().match(GET)
				.retrieve(VARIABLE)
				.parameters(key.getTaxonId(), key.getId());
		return query(Locus.class, query);
	}

	@Override
	protected boolean exists(Locus locus) {
		return get(locus.getPrimaryKey()) != null;
	}

	@Override
	protected void create(Locus locus) {
		Query query = new Query().match(TaxonRepository.GET)
				.create('(' + TaxonRepository.VARIABLE + ')' + RELATION + POST)
				.parameters(locus.getTaxonId(), locus.getId(), locus.getDescription());
		execute(query);
	}

	@Override
	protected void update(Locus locus) {
		Query query = new Query().update(GET, PUT)
				.parameters(locus.getTaxonId(), locus.getId(), locus.getDescription());
		execute(query);
	}

	@Override
	protected void delete(Locus.PrimaryKey key) {
		Query query = new Query().remove(GET, VARIABLE)
				.parameters(key.getTaxonId(), key.getId());
		execute(query);
	}

}
