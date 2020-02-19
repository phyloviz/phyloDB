package pt.ist.meic.phylodb.phylogeny.taxon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;

import java.util.List;
import java.util.Optional;

@Service
public class TaxonService {

	private TaxonRepository taxonRepository;
	private LocusRepository locusRepository;

	public TaxonService(TaxonRepository taxonRepository, LocusRepository locusRepository) {
		this.taxonRepository = taxonRepository;
		this.locusRepository = locusRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Taxon>> getTaxons(int page) {
		return Optional.ofNullable(taxonRepository.findAll(page));
	}

	@Transactional(readOnly = true)
	public Optional<Taxon> getTaxon(String id) {
		return Optional.ofNullable(taxonRepository.find(id));
	}

	@Transactional
	public boolean saveTaxon(String id, Taxon taxon) {
		if (taxon == null || !taxon.getId().equals(id))
			return false;
		taxonRepository.save(taxon);
		return true;
	}

	@Transactional
	public boolean deleteTaxon(String id) {
		if (!locusRepository.findAll(0, id).isEmpty())
			return false;
		taxonRepository.remove(id);
		return true;
	}

}
