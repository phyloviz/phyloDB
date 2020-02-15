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
	public Optional<Taxon> getTaxon(String key){
		return Optional.ofNullable(taxonRepository.find(key));
	}

	@Transactional
	public boolean saveTaxon(String key, Taxon taxon) {
		if(key == null || taxon == null || !key.equals(taxon.get_id()))
			return false;
		taxonRepository.save(taxon);
		return true;
	}

	@Transactional
	public boolean deleteTaxon(String key) {
		if(key == null || !getTaxon(key).isPresent() || !locusRepository.findAll(key).isEmpty())
			return false;
		taxonRepository.remove(key);
		return true;
	}
}
