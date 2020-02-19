package pt.ist.meic.phylodb.phylogeny.locus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.allele.AlleleRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;

import java.util.List;
import java.util.Optional;

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
	public Optional<List<Locus>> getLoci(String taxon, int page) {
		return Optional.ofNullable(locusRepository.findAll(page, taxon));
	}

	@Transactional(readOnly = true)
	public Optional<Locus> getLocus(String taxon, String locus) {
		return Optional.ofNullable(locusRepository.find(new Locus.PrimaryKey(taxon, locus)));
	}

	@Transactional
	public boolean saveLocus(String taxonId, String locusId, Locus locus) {
		if (locus == null || taxonRepository.find(taxonId) == null ||
				!locus.getTaxonId().equals(taxonId) || !locus.getId().equals(locusId))
			return false;
		locusRepository.save(locus);
		return true;
	}

	@Transactional
	public boolean deleteLocus(String taxon, String locus) {
		if (!getLocus(taxon, locus).isPresent() || !alleleRepository.findAll(0, taxon, locus).isEmpty())
			return false;
		locusRepository.remove(new Locus.PrimaryKey(taxon, locus));
		return true;
	}

}
