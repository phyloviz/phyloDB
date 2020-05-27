package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.Optional;

@Service
public class TaxonService {

	private TaxonRepository taxonRepository;

	public TaxonService(TaxonRepository taxonRepository) {
		this.taxonRepository = taxonRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<String>>> getTaxons(int page, int limit) {
		return taxonRepository.findAllEntities(page, limit);
	}

	@Transactional(readOnly = true)
	public Optional<Taxon> getTaxon(String id, Long version) {
		return taxonRepository.find(id, version);
	}

	@Transactional
	public boolean saveTaxon(Taxon taxon) {
		return taxonRepository.save(taxon);
	}

	@Transactional
	public boolean deleteTaxon(String id) {
		return taxonRepository.remove(id);
	}

}
