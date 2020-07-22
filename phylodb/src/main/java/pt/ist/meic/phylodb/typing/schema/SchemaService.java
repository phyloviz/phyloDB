package pt.ist.meic.phylodb.typing.schema;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.phylogeny.locus.LocusRepository;
import pt.ist.meic.phylodb.phylogeny.taxon.model.Taxon;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.List;
import java.util.Optional;

/**
 * Class that contains operations to manage schemas
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
@Service
public class SchemaService {

	private LocusRepository locusRepository;
	private SchemaRepository schemaRepository;

	public SchemaService(LocusRepository locusRepository, SchemaRepository schemaRepository) {
		this.locusRepository = locusRepository;
		this.schemaRepository = schemaRepository;
	}

	/**
	 * Operation to retrieve the resumed information of the requested schemas
	 *
	 * @param taxonId identifier of the {@link Taxon taxon}
	 * @param page    number of the page to retrieve
	 * @param limit   number of schemas to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link VersionedEntity<Schema.PrimaryKey>}, which is the resumed information of each schema
	 */
	@Transactional(readOnly = true)
	public Optional<List<VersionedEntity<Schema.PrimaryKey>>> getSchemas(String taxonId, int page, int limit) {
		return schemaRepository.findAllEntities(page, limit, taxonId);
	}

	/**
	 * Operation to retrieve the requested schema
	 *
	 * @param taxonId  identifier of the {@link Taxon taxon}
	 * @param schemaId identifier of the {@link Schema schema}
	 * @param version  version of the schema
	 * @return an {@link Optional} of {@link Schema}, which is the requested schema
	 */
	@Transactional(readOnly = true)
	public Optional<Schema> getSchema(String taxonId, String schemaId, long version) {
		return schemaRepository.find(new Schema.PrimaryKey(taxonId, schemaId), version);
	}

	/**
	 * Operation to save a schema
	 *
	 * @param schema schema to be saved
	 * @return {@code true} if the schema was saved
	 */
	@Transactional
	public boolean saveSchema(Schema schema) {
		if (schema == null)
			return false;
		String[] lociIds = schema.getLociIds().toArray(new String[0]);
		Optional<Schema> dbSchema = schemaRepository.find(schema.getPrimaryKey().getTaxonId(), schema.getType(), lociIds);
		if (locusRepository.anyMissing(schema.getLociReferences()) ||
				(dbSchema.isPresent() && !dbSchema.get().getPrimaryKey().equals(schema.getPrimaryKey())))
			return false;
		return schemaRepository.save(schema);
	}

	/**
	 * Operation to deprecate a schema
	 *
	 * @param taxonId  identifier of the {@link Taxon taxon}
	 * @param schemaId identifier of the {@link Schema schema}
	 * @return {@code true} if the schema was deprecated
	 */
	@Transactional
	public boolean deleteSchema(String taxonId, String schemaId) {
		return schemaRepository.remove(new Schema.PrimaryKey(taxonId, schemaId));
	}

}
