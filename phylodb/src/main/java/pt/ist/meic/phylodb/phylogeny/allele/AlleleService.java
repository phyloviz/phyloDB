package pt.ist.meic.phylodb.phylogeny.allele;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.io.formatters.dataset.allele.FastaFormatter;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.utils.db.BatchRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlleleService {

	private LocusRepository locusRepository;
	private AlleleRepository alleleRepository;

	public AlleleService(LocusRepository locusRepository, AlleleRepository alleleRepository) {
		this.locusRepository = locusRepository;
		this.alleleRepository = alleleRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Allele>> getAlleles(String taxonId, String locusId, UUID project, int page, int limit) {
		return alleleRepository.findAll(page, limit, taxonId, locusId, project);
	}

	@Transactional(readOnly = true)
	public Optional<Allele> getAllele(String taxonId, String locusId, String alleleId, UUID project, long version) {
		return alleleRepository.find(new Allele.PrimaryKey(taxonId, locusId, alleleId, project), version);
	}

	@Transactional
	public boolean saveAllele(Allele allele) {
		if(allele == null) return false;
		return locusRepository.exists(new Locus.PrimaryKey(allele.getTaxonId(), allele.getLocusId())) &&
				alleleRepository.save(allele);
	}

	@Transactional
	public boolean deleteAllele(String taxonId, String locusId, String alleleId, UUID project) {
		return alleleRepository.remove(new Allele.PrimaryKey(taxonId, locusId, alleleId, project));
	}

	@Transactional
	public boolean saveAllelesOnConflictSkip(String taxonId, String locusId, UUID project, MultipartFile file) throws IOException {
		return saveAll(taxonId, locusId, project, BatchRepository.SKIP, file);
	}

	@Transactional
	public boolean saveAllelesOnConflictUpdate(String taxonId, String locusId, UUID project, MultipartFile file) throws IOException {
		return saveAll(taxonId, locusId, project, BatchRepository.UPDATE, file);
	}

	private boolean saveAll(String taxonId, String locusId, UUID project, String conflict, MultipartFile file) throws IOException {
		if (!locusRepository.exists(new Locus.PrimaryKey(taxonId, locusId)))
			return false;
		List<Allele> alleles = new FastaFormatter().parse(file, taxonId, locusId, project);
		String p = project != null ? project.toString() : null;
		return alleleRepository.saveAll(alleles, conflict, taxonId, locusId, p);
	}

}
