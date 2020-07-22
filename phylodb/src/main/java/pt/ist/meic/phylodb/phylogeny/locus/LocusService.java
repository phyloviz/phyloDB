package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.Optional;

/**
 * Class that contains operations to manage loci
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
@Service
public class LocusService {

	private TaxonRepository taxonRepository;
	private LocusRepository locusRepository;

	public LocusService(TaxonRepository taxonRepository, LocusRepository locusRepository) {
		this.taxonRepository = taxonRepository;
		this.locusRepository = locusRepository;
	}

	/**
	 * Operation to retrieve the resumed information of the requested loci
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param page    number of the page to retrieve
	 * @param limit   number of loci to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link VersionedEntity<Locus.PrimaryKey>}, which is the resumed information of each locus
	 */
	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<Locus.PrimaryKey>>> getLoci(String taxonId, int page, int limit) {
		return locusRepository.findAllEntities(page, limit, taxonId);
	}

	/**
	 * Operation to retrieve the requested locus
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @param version version of the locus
	 * @return an {@link Optional} of {@link Locus}, which is the requested locus
	 */
	@Transactional(readOnly = true)
	public Optional<Locus> getLocus(String taxonId, String locusId, Long version) {
		return locusRepository.find(new Locus.PrimaryKey(taxonId, locusId), version);
	}

	/**
	 * Operation to save a locus
	 *
	 * @param locus locus to be saved
	 * @return {@code true} if the locus was saved
	 */
	@Transactional
	public boolean saveLocus(Locus locus) {
		if (locus == null)
			return false;
		return taxonRepository.exists(locus.getTaxonId()) && locusRepository.save(locus);
	}

	/**
	 * Operation to deprecate a locus
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @return {@code true} if the locus was deprecated
	 */
	@Transactional
	public boolean deleteLocus(String taxonId, String locusId) {
		return locusRepository.remove(new Locus.PrimaryKey(taxonId, locusId));
	}

}
