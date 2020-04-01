package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.utils.db.Status;

import java.util.List;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

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
		return Optional.ofNullable(locusRepository.findAll(page, limit, taxonId));
	}

	@Transactional(readOnly = true)
	public Optional<Locus> getLocus(String taxonId, String locusId, int version) {
		return Optional.ofNullable(locusRepository.find(new Locus.PrimaryKey(taxonId, locusId), version));
	}

	@Transactional
	public Status saveLocus(Locus locus) {
		return taxonRepository.exists(locus.getTaxonId()) ? locusRepository.save(locus) : UNCHANGED;
	}

	@Transactional
	public Status deleteLocus(String taxonId, String locusId) {
		return locusRepository.remove(new Locus.PrimaryKey(taxonId, locusId));
	}

}
