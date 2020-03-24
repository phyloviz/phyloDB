package pt.ist.meic.phylodb.phylogeny.allele;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.exception.FileFormatException;
import pt.ist.meic.phylodb.formatters.dataset.allele.FastaFormatter;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.utils.service.StatusResult;

import java.util.List;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.Status.*;

@Service
public class AlleleService {

	private LocusRepository locusRepository;
	private AlleleRepository alleleRepository;

	public AlleleService(LocusRepository locusRepository, AlleleRepository alleleRepository) {
		this.locusRepository = locusRepository;
		this.alleleRepository = alleleRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Allele>> getAlleles(String taxonId, String locusId, int page, int limit) {
		return Optional.ofNullable(alleleRepository.findAll(page, limit, taxonId, locusId));
	}

	@Transactional(readOnly = true)
	public Optional<Allele> getAllele(String taxonId, String locusId, String alleleId) {
		return Optional.ofNullable(alleleRepository.find(new Allele.PrimaryKey(taxonId, locusId, alleleId)));
	}

	@Transactional
	public StatusResult saveAllele(Allele allele) {
		if (locusRepository.find(new Locus.PrimaryKey(allele.getTaxonId(), allele.getLocusId())) == null)
			return new StatusResult(UNCHANGED);
		return new StatusResult(alleleRepository.save(allele));
	}

	@Transactional
	public StatusResult deleteAllele(String taxonId, String locusId, String alleleId) {
		if (!getAllele(taxonId, locusId, alleleId).isPresent())
			return new StatusResult(UNCHANGED);
		return new StatusResult(alleleRepository.remove(new Allele.PrimaryKey(taxonId, locusId, alleleId)));
	}

	@Transactional
	public StatusResult saveAllelesOnConflictUpdate(String taxonId, String locusId, MultipartFile file) throws FileFormatException {
		if (locusRepository.find(new Locus.PrimaryKey(taxonId, locusId)) == null)
			return new StatusResult(UNCHANGED);
		alleleRepository.saveAllOnConflictUpdate(taxonId, locusId, new FastaFormatter().read(file).getEntities());
		return new StatusResult(UPDATED);
	}

	@Transactional
	public StatusResult saveAllelesOnConflictSkip(String taxonId, String locusId, MultipartFile file) throws FileFormatException {
		if (locusRepository.find(new Locus.PrimaryKey(taxonId, locusId)) == null)
			return new StatusResult(UNCHANGED);
		alleleRepository.saveAllOnConflictSkip(taxonId, locusId, new FastaFormatter().read(file).getEntities());
		return new StatusResult(CREATED);
	}

}
