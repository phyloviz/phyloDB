package pt.ist.meic.phylodb.phylogeny.allele;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.formatters.datasets.Dataset;
import pt.ist.meic.phylodb.formatters.datasets.DatasetFormatter;
import pt.ist.meic.phylodb.formatters.datasets.FastaFormatter;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AlleleService {

	private LocusRepository locusRepository;
	private AlleleRepository alleleRepository;

	public AlleleService(LocusRepository locusRepository, AlleleRepository alleleRepository) {
		this.locusRepository = locusRepository;
		this.alleleRepository = alleleRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Allele>> getAlleles(String taxon, String locus, int page) {
		return Optional.ofNullable(alleleRepository.findAll(page, taxon, locus));
	}

	@Transactional(readOnly = true)
	public Optional<Allele> getAllele(String taxon, String locus, String allele) {
		return Optional.ofNullable(alleleRepository.find(new Allele.PrimaryKey(taxon, locus, allele)));
	}

	@Transactional
	public boolean saveAlleles(String taxon, String locus, MultipartFile file) throws IOException {
		Optional<DatasetFormatter<?>> optional = DatasetFormatter.get(DatasetFormatter.FASTA);
		if (!optional.isPresent() || locusRepository.find(new Locus.PrimaryKey(taxon, locus)) == null)
			return false;
		FastaFormatter formatter = (FastaFormatter) optional.get();
		Dataset<Allele> alleles = formatter.read(file);
		alleleRepository.saveAll(taxon, locus, alleles);
		return true;
	}

	@Transactional
	public boolean saveAllele(String taxon, String locus, String alleleId, Allele allele) {
		if (taxon == null || locus == null || locusRepository.find(new Locus.PrimaryKey(taxon, locus)) == null ||
				!allele.getTaxonId().equals(taxon) || !allele.getLocusId().equals(locus) || !allele.getId().equals(alleleId))
			return false;
		alleleRepository.save(allele);
		return true;
	}

	@Transactional
	public boolean deleteAllele(String taxon, String locus, String allele) {
		if (!getAllele(taxon, locus, allele).isPresent())
			return false;
		alleleRepository.remove(new Allele.PrimaryKey(taxon, locus, allele));
		return true;
	}

}
