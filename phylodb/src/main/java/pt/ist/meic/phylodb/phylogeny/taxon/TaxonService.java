package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.Optional;

/**
 * Class that contains operations to manage taxons
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
@Service
public class TaxonService extends pt.ist.meic.phylodb.utils.service.Service  {

	private TaxonRepository taxonRepository;

	public TaxonService(TaxonRepository taxonRepository) {
		this.taxonRepository = taxonRepository;
	}

	/**
	 * Operation to retrieve the resumed information of the requested taxons
	 *
	 * @param page  number of the page to retrieve
	 * @param limit number of taxons to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link VersionedEntity<String>}, which is the resumed information of each taxon
	 */
	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<String>>> getTaxons(int page, int limit) {
		return taxonRepository.findAllEntities(page, limit);
	}

	/**
	 * Operation to retrieve the requested taxon
	 *
	 * @param id      identifier of the {@link Taxon taxon}
	 * @param version version of the taxon
	 * @return an {@link Optional} of {@link Taxon}, which is the requested taxon
	 */
	@Transactional(readOnly = true)
	public Optional<Taxon> getTaxon(String id, Long version) {
		return taxonRepository.find(id, version);
	}

	/**
	 * Operation to save a taxon
	 *
	 * @param taxon taxon to be saved
	 * @return {@code true} if the taxon was saved
	 */
	@Transactional
	public boolean saveTaxon(Taxon taxon) {
		return taxonRepository.save(taxon);
	}

	/**
	 * Operation to deprecate a taxon
	 *
	 * @param id identifier of the {@link Taxon taxon}
	 * @return {@code true} if the taxon was deprecated
	 */
	@Transactional
	public boolean deleteTaxon(String id) {
		return taxonRepository.remove(id);
	}

}
