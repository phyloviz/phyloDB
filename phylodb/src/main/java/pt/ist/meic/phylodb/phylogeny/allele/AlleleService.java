package pt.ist.meic.phylodb.phylogeny.allele;

import javafx.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.io.formatters.dataset.allele.FastaFormatter;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class AlleleService {

	private LocusRepository locusRepository;
	private AlleleRepository alleleRepository;

	public AlleleService(LocusRepository locusRepository, AlleleRepository alleleRepository) {
		this.locusRepository = locusRepository;
		this.alleleRepository = alleleRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<Allele.PrimaryKey>>> getAllelesEntities(String taxonId, String locusId, String project, int page, int limit) {
		return alleleRepository.findAllEntities(page, limit, taxonId, locusId, project);
	}

	@Transactional(readOnly = true)
	public Optional<List<Allele>> getAlleles(String taxonId, String locusId, String project, int page, int limit) {
		return alleleRepository.findAll(page, limit, taxonId, locusId, project);
	}

	@Transactional(readOnly = true)
	public Optional<Allele> getAllele(String taxonId, String locusId, String alleleId, String project, long version) {
		return alleleRepository.find(new Allele.PrimaryKey(taxonId, locusId, alleleId, project), version);
	}

	@Transactional
	public boolean saveAllele(Allele allele) {
		if (allele == null) return false;
		return locusRepository.exists(new Locus.PrimaryKey(allele.getTaxonId(), allele.getLocusId())) &&
				alleleRepository.save(allele);
	}

	@Transactional
	public boolean deleteAllele(String taxonId, String locusId, String alleleId, String project) {
		return alleleRepository.remove(new Allele.PrimaryKey(taxonId, locusId, alleleId, project));
	}

	@Transactional
	public Optional<Pair<Integer[], String[]>> saveAllelesOnConflictSkip(String taxonId, String locusId, String project, MultipartFile file) throws IOException {
		return saveAll(taxonId, locusId, project, false, file);
	}

	@Transactional
	public Optional<Pair<Integer[], String[]>> saveAllelesOnConflictUpdate(String taxonId, String locusId, String project, MultipartFile file) throws IOException {
		return saveAll(taxonId, locusId, project, true, file);
	}

	private Optional<Pair<Integer[], String[]>> saveAll(String taxonId, String locusId, String project, boolean conflict, MultipartFile file) throws IOException {
		if (!locusRepository.exists(new Locus.PrimaryKey(taxonId, locusId)))
			return Optional.empty();
		Predicate<Allele> canSave = conflict ? a -> true : a -> !alleleRepository.exists(a.getPrimaryKey());
		Pair<List<Allele>, List<Integer>> parsed = new FastaFormatter().parse(file, taxonId, locusId, project);
		List<String> invalids = new ArrayList<>();
		List<Allele> alleles = parsed.getKey(), toSave = new ArrayList<>();
		for (Allele allele : alleles) {
			if (canSave.test(allele)) {
				toSave.add(allele);
				continue;
			}
			invalids.add(allele.getPrimaryKey().getId());
		}
		return alleleRepository.saveAll(toSave) ?
				Optional.of(new Pair<>(parsed.getValue().toArray(new Integer[0]), invalids.toArray(new String[0]))) :
				Optional.empty();
	}

}
