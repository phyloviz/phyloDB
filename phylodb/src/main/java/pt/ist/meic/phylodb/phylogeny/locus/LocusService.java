package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleRepository;
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
	private AlleleRepository alleleRepository;

	public LocusService(TaxonRepository taxonRepository, LocusRepository locusRepository, AlleleRepository alleleRepository) {
		this.taxonRepository = taxonRepository;
		this.locusRepository = locusRepository;
		this.alleleRepository = alleleRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Locus>> getLoci(String taxon, int page, int limit) {
		return Optional.ofNullable(locusRepository.findAll(page, limit, taxon));
	}

	@Transactional(readOnly = true)
	public Optional<Locus> getLocus(String taxon, String locus) {
		return Optional.ofNullable(locusRepository.find(new Locus.PrimaryKey(taxon, locus)));
	}

	@Transactional
	public StatusResult saveLocus(String taxonId, String locusId, Locus locus) {
		if (locus == null || taxonRepository.find(taxonId) == null ||
				!locus.getTaxonId().equals(taxonId) || !locus.getId().equals(locusId))
			return new StatusResult(UNCHANGED);
		return new StatusResult(locusRepository.save(locus));
	}

	@Transactional
	public StatusResult deleteLocus(String taxon, String locus) {
		//todo
		if (!getLocus(taxon, locus).isPresent() || !alleleRepository.findAll(0, 1, taxon, locus).isEmpty())
			return new StatusResult(UNCHANGED);
		return new StatusResult(locusRepository.remove(new Locus.PrimaryKey(taxon, locus)));
	}

}
