package pt.ist.meic.phylodb.phylogeny.allele;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.io.formatters.dataset.allele.FastaFormatter;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.utils.service.Pair;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Class that contains operations to manage alleles
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
@Service
public class AlleleService extends pt.ist.meic.phylodb.utils.service.Service  {

	private LocusRepository locusRepository;
	private AlleleRepository alleleRepository;

	public AlleleService(LocusRepository locusRepository, AlleleRepository alleleRepository) {
		this.locusRepository = locusRepository;
		this.alleleRepository = alleleRepository;
	}

	/**
	 * Operation to retrieve the resumed information of the requested alleles
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @param project identifier of the {@link Project project} (optional)
	 * @param page    number of the page to retrieve
	 * @param limit   number of alleles to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link VersionedEntity<Allele.PrimaryKey>}, which is the resumed information of each allele
	 */
	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<Allele.PrimaryKey>>> getAllelesEntities(String taxonId, String locusId, String project, int page, int limit) {
		return alleleRepository.findAllEntities(page, limit, taxonId, locusId, project);
	}

	/**
	 * Operation to retrieve the information of the requested alleles
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @param project identifier of the {@link Project project} (optional)
	 * @param page    number of the page to retrieve
	 * @param limit   number of alleles to retrieve by page
	 * @return an {@link Optional} with a {@link List<Allele>} which is the information of each allele
	 */
	@Transactional(readOnly = true)
	public Optional<List<Allele>> getAlleles(String taxonId, String locusId, String project, int page, int limit) {
		return alleleRepository.findAll(page, limit, taxonId, locusId, project);
	}

	/**
	 * Operation to retrieve the requested allele
	 *
	 * @param taxonId  identifier of the {@link Taxon taxon}
	 * @param locusId  identifier of the {@link Locus locus}
	 * @param alleleId identifier of the {@link Allele allele}
	 * @param project  identifier of the {@link Project project} (optional)
	 * @param version  version of the allele
	 * @return an {@link Optional} of {@link Allele}, which is the requested allele
	 */
	@Transactional(readOnly = true)
	public Optional<Allele> getAllele(String taxonId, String locusId, String alleleId, String project, long version) {
		return alleleRepository.find(new Allele.PrimaryKey(taxonId, locusId, alleleId, project), version);
	}

	/**
	 * Operation to save an allele
	 *
	 * @param allele allele to be saved
	 * @return {@code true} if the allele was saved
	 */
	@Transactional
	public boolean saveAllele(Allele allele) {
		if (allele == null) return false;
		return locusRepository.exists(new Locus.PrimaryKey(allele.getTaxonId(), allele.getLocusId())) &&
				alleleRepository.save(allele);
	}

	/**
	 * Operation to deprecate an allele
	 *
	 * @param taxonId  identifier of the {@link Taxon taxon}
	 * @param locusId  identifier of the {@link Locus locus}
	 * @param alleleId identifier of the {@link Allele allele}
	 * @param project  identifier of the {@link Project project} (optional)
	 * @return {@code true} if the allele was deprecated
	 */
	@Transactional
	public boolean deleteAllele(String taxonId, String locusId, String alleleId, String project) {
		return alleleRepository.remove(new Allele.PrimaryKey(taxonId, locusId, alleleId, project));
	}

	/**
	 * Operation to save several alleles if they don't exist
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @param project identifier of the {@link Project project} (optional)
	 * @param file    file with the alleles in the fasta format
	 * @return an {@link Optional} of {@link Pair} where the key is the list of line numbers that couldn't be parsed, and the value is list of alleles ids parsed that are not valid
	 * @throws IOException if there is an error parsing the file
	 */
	@Transactional
	public Optional<Pair<Integer[], String[]>> saveAllelesOnConflictSkip(String taxonId, String locusId, String project, MultipartFile file) throws IOException {
		return saveAll(taxonId, locusId, project, false, file);
	}

	/**
	 * Operation to save several alleles
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param locusId identifier of the {@link Locus locus}
	 * @param project identifier of the {@link Project project} (optional)
	 * @param file    file with the alleles in the fasta format
	 * @return an {@link Optional} of {@link Pair} where the key is the list of line numbers that couldn't be parsed, and the value is list of alleles ids parsed that are not valid
	 * @throws IOException if there is an error parsing the file
	 */
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
