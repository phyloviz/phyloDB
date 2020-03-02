package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.utils.service.StatusResult;

import java.util.List;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

@Service
public class TaxonService {

	private TaxonRepository taxonRepository;
	private LocusRepository locusRepository;

	public TaxonService(TaxonRepository taxonRepository, LocusRepository locusRepository) {
		this.taxonRepository = taxonRepository;
		this.locusRepository = locusRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Taxon>> getTaxons(int page, int limit) {
		return Optional.ofNullable(taxonRepository.findAll(page, limit));
	}

	@Transactional(readOnly = true)
	public Optional<Taxon> getTaxon(String id) {
		return Optional.ofNullable(taxonRepository.find(id));
	}

	@Transactional
	public StatusResult saveTaxon(String id, Taxon taxon) {
		if (taxon == null || !taxon.getId().equals(id))
			return new StatusResult(UNCHANGED);
		return new StatusResult(taxonRepository.save(taxon));
	}

	@Transactional
	public StatusResult deleteTaxon(String id) {
		//todo
		if (!locusRepository.findAll(0, 1, id).isEmpty())
			return new StatusResult(UNCHANGED);
		return new StatusResult(taxonRepository.remove(id));
	}

}
