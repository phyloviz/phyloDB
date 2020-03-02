package pt.ist.meic.phylodb.phylogeny.allele;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;

import java.util.List;

@Repository
public class AlleleRepository extends EntityRepository<Allele, Allele.PrimaryKey> {


	private static final String RELATION = "-[:CONTAINS]->", PATH = LocusRepository.GET + RELATION;
	public static final String VARIABLE = "a", LABEL = "Allele", ORDER = String.format("%s.id", VARIABLE),
			GET_ALL = PATH + String.format("(%s:%s)", VARIABLE, LABEL),
			GET = PATH + String.format("(%s:%s {id: $2})", VARIABLE, LABEL),
			POST = String.format("(:%s {id: $2, sequence: $3})", LABEL),
			PUT = String.format("%s.sequence = $3", VARIABLE),
			POST_ALL = String.format("(:%s {%s})", LABEL, "id: $%s, sequence: $%s"),
			PUT_ALL = String.format("%s.sequence = %s",VARIABLE, "$%s");

	public AlleleRepository(Session session) {
		super(session);
	}

	@Override
	protected List<Allele> getAll(int page, int limit, Object... filters) {
		if (filters == null || filters.length == 0)
			return null;
		Query query = new Query().match(GET_ALL)
				.retrieve(VARIABLE)
				.page(ORDER)
				.parameters(filters[0], filters[1], page, limit);
		return queryAll(Allele.class, query);
	}

	@Override
	protected Allele get(Allele.PrimaryKey key) {
		Query query = new Query().match(GET)
				.retrieve(VARIABLE)
				.parameters(key.getTaxonId(), key.getLocusId(),  key.getId());
		return query(Allele.class, query);
	}

	@Override
	protected boolean exists(Allele allele) {
		return get(allele.getPrimaryKey()) != null;
	}

	@Override
	protected void create(Allele allele) {
		Query query = new Query().match(LocusRepository.GET)
				.create('(' + LocusRepository.VARIABLE + ')' + RELATION + POST)
				.parameters(allele.getTaxonId(),  allele.getLocusId(), allele.getId(), allele.getSequence());
		execute(query);
	}

	@Override
	protected void update(Allele allele) {
		Query query = new Query().update(GET, PUT)
				.parameters(allele.getTaxonId(),  allele.getLocusId(), allele.getId(), allele.getSequence());
		execute(query);
	}

	@Override
	protected void delete(Allele.PrimaryKey key) {
		Query query = new Query().remove(GET, VARIABLE)
			.parameters(key.getTaxonId(), key.getLocusId(), key.getId());
		execute(query);
	}

	public void saveAllOnConflictUpdate(String taxon, String locus, List<Allele> alleles) {
		Query query = new Query().match(LocusRepository.GET)
				.parameters(taxon, locus)
				.with(LocusRepository.VARIABLE);
		for (int i = 0, p = 2; i < alleles.size(); i++) {
			if(find(new Allele.PrimaryKey(taxon, locus, alleles.get(i).getId())) != null)
				query.update(String.format('(' + LocusRepository.VARIABLE + ')' + RELATION + "(%s:%s {id: $%s})", VARIABLE, LABEL, p++), String.format(PUT_ALL, p++))
						.parameters(alleles.get(i).getId(), alleles.get(i).getSequence());
			else
				query.create(String.format('(' + LocusRepository.VARIABLE + ')' + RELATION + POST_ALL, p++, p++))
						.parameters(alleles.get(i).getId(), alleles.get(i).getSequence());
			if(i != alleles.size() - 1)
				query.with(LocusRepository.VARIABLE);
		}
		execute(query);
	}

	public void saveAllOnConflictSkip(String taxon, String locus, List<Allele> alleles) {
		Query query = new Query().match(LocusRepository.GET)
				.parameters(taxon, locus)
				.with(LocusRepository.VARIABLE);
		for (int i = 0, p = 2; i < alleles.size(); i++) {
			if(find(new Allele.PrimaryKey(taxon, locus, alleles.get(i).getId())) != null) {
				LOG.info("The allele " + alleles.get(i).getId() + " with sequence " + alleles.get(i).getSequence() + " could not be created since it already exists");
				continue;
			}
			query.create(String.format('(' + LocusRepository.VARIABLE + ')' + RELATION + POST_ALL, p++, p++))
					.parameters(alleles.get(i).getId(), alleles.get(i).getSequence());
			if(i != alleles.size() - 1)
				query.with(LocusRepository.VARIABLE);
		}
		execute(query);
	}

}
