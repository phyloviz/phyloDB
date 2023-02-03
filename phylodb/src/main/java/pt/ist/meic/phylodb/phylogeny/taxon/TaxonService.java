package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;
import pt.ist.meic.phylodb.utils.service.VersionedEntityService;

import java.util.List;
import java.util.Optional;

/**
 * Class that contains operations to manage taxa
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
@Service
public class TaxonService extends VersionedEntityService<Taxon, String> {

	private TaxonRepository taxonRepository;

	public TaxonService(TaxonRepository taxonRepository) {
		this.taxonRepository = taxonRepository;
	}

	/**
	 * Operation to retrieve the resumed information of the requested taxa
	 *
	 * @param page  number of the page to retrieve
	 * @param limit number of taxa to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link VersionedEntity<String>}, which is the resumed information of each taxon
	 */
	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<String>>> getTaxa(int page, int limit) {
		return getAllEntities(page, limit);
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
		return get(id, version);
	}

	/**
	 * Operation to save a taxon
	 *
	 * @param taxon taxon to be saved
	 * @return {@code true} if the taxon was saved
	 */
	@Transactional
	public boolean saveTaxon(Taxon taxon) {
		return save(taxon);
	}

	/**
	 * Operation to deprecate a taxon
	 *
	 * @param id identifier of the {@link Taxon taxon}
	 * @return {@code true} if the taxon was deprecated
	 */
	@Transactional
	public boolean deleteTaxon(String id) {
		return remove(id);
	}

	@Override
	protected Optional<List<VersionedEntity<String>>> getAllEntities(int page, int limit, Object... params) {
		return taxonRepository.findAllEntities(page, limit);
	}

	@Override
	protected Optional<Taxon> get(String key, long version) {
		return taxonRepository.find(key, version);
	}

	@Override
	protected boolean save(Taxon entity) {
		return taxonRepository.save(entity);
	}

	@Override
	protected boolean remove(String key) {
		return taxonRepository.remove(key);
	}

}
