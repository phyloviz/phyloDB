package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.db.Status;

import java.util.List;
import java.util.Optional;

@Service
public class TaxonService {

	private TaxonRepository taxonRepository;

	public TaxonService(TaxonRepository taxonRepository) {
		this.taxonRepository = taxonRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Taxon>> getTaxons(int page, int limit) {
		return Optional.ofNullable(taxonRepository.findAll(page, limit));
	}

	@Transactional(readOnly = true)
	public Optional<Taxon> getTaxon(String id, int version) {
		return Optional.ofNullable(taxonRepository.find(id, version));
	}

	@Transactional
	public Status saveTaxon(Taxon taxon) {
		return taxonRepository.save(taxon);
	}

	@Transactional
	public Status deleteTaxon(String id) {
		return taxonRepository.remove(id);
	}

}
