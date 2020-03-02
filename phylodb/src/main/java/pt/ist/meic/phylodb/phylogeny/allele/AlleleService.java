package pt.ist.meic.phylodb.phylogeny.allele;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.exception.FileFormatException;
import pt.ist.meic.phylodb.formatters.datasets.Dataset;
import pt.ist.meic.phylodb.formatters.datasets.DatasetFormatter;
import pt.ist.meic.phylodb.formatters.datasets.FastaFormatter;
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
	public Optional<List<Allele>> getAlleles(String taxon, String locus, int page, int limit) {
		return Optional.ofNullable(alleleRepository.findAll(page, limit, taxon, locus));
	}

	@Transactional(readOnly = true)
	public Optional<Allele> getAllele(String taxon, String locus, String allele) {
		return Optional.ofNullable(alleleRepository.find(new Allele.PrimaryKey(taxon, locus, allele)));
	}

	@Transactional
	public StatusResult saveAllele(String taxon, String locus, String alleleId, Allele allele) {
		if (taxon == null || locus == null || locusRepository.find(new Locus.PrimaryKey(taxon, locus)) == null ||
				!allele.getTaxonId().equals(taxon) || !allele.getLocusId().equals(locus) || !allele.getId().equals(alleleId))
			return new StatusResult(UNCHANGED);
		return new StatusResult(alleleRepository.save(allele));
	}

	@Transactional
	public StatusResult deleteAllele(String taxon, String locus, String allele) {
		//todo
		if (!getAllele(taxon, locus, allele).isPresent())
			return new StatusResult(UNCHANGED);
		return new StatusResult(alleleRepository.remove(new Allele.PrimaryKey(taxon, locus, allele)));
	}

	@Transactional
	public StatusResult saveAllelesOnConflictUpdate(String taxon, String locus, MultipartFile file) throws FileFormatException {
		if (locusRepository.find(new Locus.PrimaryKey(taxon, locus)) == null)
			return new StatusResult(UNCHANGED);
		alleleRepository.saveAllOnConflictUpdate(taxon, locus, readDataset(file).getEntities());
		return new StatusResult(UPDATED);
	}

	@Transactional
	public StatusResult saveAllelesOnConflictSkip(String taxon, String locus, MultipartFile file) throws FileFormatException {
		if (locusRepository.find(new Locus.PrimaryKey(taxon, locus)) == null)
			return new StatusResult(UNCHANGED);
		alleleRepository.saveAllOnConflictSkip(taxon, locus, readDataset(file).getEntities());
		return new StatusResult(CREATED);
	}

	private Dataset<Allele> readDataset(MultipartFile file) throws FileFormatException {
		FastaFormatter formatter = (FastaFormatter) DatasetFormatter.get(DatasetFormatter.FASTA).get();
		return formatter.read(file);
	}

}
