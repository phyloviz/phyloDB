package pt.ist.meic.phylodb.typing.schema;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.taxon.TaxonRepository;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.StatusResult;

import java.util.List;
import java.util.Optional;

import static pt.ist.meic.phylodb.utils.db.Status.UNCHANGED;

@Service
public class SchemaService {

	private TaxonRepository taxonRepository;
	private LocusRepository locusRepository;
	private SchemaRepository schemaRepository;

	public SchemaService(TaxonRepository taxonRepository, LocusRepository locusRepository, SchemaRepository schemaRepository) {
		this.taxonRepository = taxonRepository;
		this.locusRepository = locusRepository;
		this.schemaRepository = schemaRepository;
	}

	@Transactional(readOnly = true)
	public Optional<List<Schema>> getSchemas(String taxonId, Integer page, int limit) {
		return Optional.ofNullable(schemaRepository.findAll(page, limit, taxonId));
	}

	@Transactional(readOnly = true)
	public Optional<Schema> getSchema(String taxonId, String schemaId) {
		return Optional.ofNullable(schemaRepository.find(new Schema.PrimaryKey(taxonId, schemaId)));
	}

	@Transactional
	public StatusResult saveSchema(Schema schema) {
		if (!Schema.METHODS.contains(schema.getType()) || taxonRepository.find(schema.getTaxonId()) == null ||
				!locusRepository.existsAll(schema.getTaxonId(), schema.getLociIds()))
			return new StatusResult(UNCHANGED);
		return new StatusResult(schemaRepository.save(schema));
	}

	@Transactional
	public StatusResult deleteSchema(String taxonId, String schemaId) {
		if (!getSchema(taxonId, schemaId).isPresent())
			return new StatusResult(UNCHANGED);
		return new StatusResult(schemaRepository.remove(new Schema.PrimaryKey(taxonId, schemaId)));
	}

}
