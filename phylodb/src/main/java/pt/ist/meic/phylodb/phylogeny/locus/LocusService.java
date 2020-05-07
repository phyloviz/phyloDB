package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LocusService {

	private TaxonRepository taxonRepository;
	private LocusRepository locusRepository;

	public LocusService(TaxonRepository taxonRepository, LocusRepository locusRepository) {
		this.taxonRepository = taxonRepository;
		this.locusRepository = locusRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Locus>> getLoci(String taxonId, int page, int limit) {
		return locusRepository.findAll(page, limit, taxonId);
	}

	@Transactional(readOnly = true)
	public Optional<Locus> getLocus(String taxonId, String locusId, Long version) {
		return locusRepository.find(new Locus.PrimaryKey(taxonId, locusId), version);
	}

	@Transactional
	public boolean saveLocus(Locus locus) {
		if (locus == null)
			return false;
		return taxonRepository.exists(locus.getTaxonId()) && locusRepository.save(locus);
	}

	@Transactional
	public boolean deleteLocus(String taxonId, String locusId) {
		return locusRepository.remove(new Locus.PrimaryKey(taxonId, locusId));
	}

}
