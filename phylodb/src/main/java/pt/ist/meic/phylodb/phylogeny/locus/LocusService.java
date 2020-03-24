package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.utils.service.StatusResult;

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
	public Optional<Locus> getLocus(String taxonId, String locusId) {
		return Optional.ofNullable(locusRepository.find(new Locus.PrimaryKey(taxonId, locusId)));
	}

	@Transactional
	public StatusResult saveLocus(Locus locus) {
		if (taxonRepository.find(locus.getTaxonId()) == null)
			return new StatusResult(UNCHANGED);
		return new StatusResult(locusRepository.save(locus));
	}

	@Transactional
	public StatusResult deleteLocus(String taxonId, String locusId) {
		if (!getLocus(taxonId, locusId).isPresent())
			return new StatusResult(UNCHANGED);
		return new StatusResult(locusRepository.remove(new Locus.PrimaryKey(taxonId, locusId)));
	}

}
