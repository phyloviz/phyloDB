package pt.ist.meic.phylodb.phylogeny.taxon;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.List;

@Repository
public class TaxonRepository extends EntityRepository<Taxon, String> {

	public static final String VARIABLE = "t", LABEL = "Taxon", ORDER = String.format("%s.id", VARIABLE),
			GET_ALL = String.format("(%s:%s)", VARIABLE, LABEL),
			GET = String.format("(%s:%s {id: $0})", VARIABLE, LABEL),
			POST = String.format("(:%s {id: $0, description: $1})", LABEL),
			PUT = String.format("%s.description = $1", VARIABLE);

	public TaxonRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Taxon> getAll(int page, int limit, Object... filters) {
		Query query = new Query().match(GET_ALL)
				.retrieve(VARIABLE)
				.page(ORDER)
				.parameters(page, limit);
		return queryAll(Taxon.class, query);
	}

	@Override
	protected Taxon get(String key) {
		Query query = new Query().match(GET)
				.retrieve(VARIABLE)
				.parameters(key);
		return query(Taxon.class, query);
	}

	@Override
	protected boolean exists(Taxon taxon) {
		return get(taxon.getId()) != null;
	}

	@Override
	protected void create(Taxon taxon) {
		Query query = new Query().create(POST)
				.parameters(taxon.getId(), taxon.getDescription());
		execute(query);
	}

	@Override
	protected void update(Taxon taxon) {
		Query query = new Query().update(GET, PUT)
				.parameters(taxon.getId(), taxon.getDescription());
		execute(query);
	}

	@Override
	protected void delete(String key) {
		Query query = new Query().remove(GET, VARIABLE)
				.parameters(key);
		execute(query);
	}

}
