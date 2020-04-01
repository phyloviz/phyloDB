package pt.ist.meic.phylodb.typing.schema.model;

import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.Reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION_VALUE;

public class Schema extends Entity {

	public static final String MLST = "mlst", MLVA = "mlva", SNP = "snp";
	public static final List<String> METHODS = new ArrayList<String>() {{
		add(MLST);
		add(MLVA);
		add(SNP);
	}};

	private String taxonId;
	private String id;
	private String type;
	private String description;
	private List<Reference<String>> lociIds;

	public Schema(String taxonId, String id, int version, boolean deprecated, String type, String description, List<Reference<String>> lociIds) {
		super(version, deprecated);
		this.taxonId = taxonId;
		this.id = id;
		this.type = type;
		this.description = description;
		this.lociIds = lociIds;
	}

	public Schema(String taxonId, String id, String type, String description, String[] lociId) {
		this(taxonId, id, -1, false, type, description,  Arrays.stream(lociId)
				.map(i -> new Reference<>(i, CURRENT_VERSION_VALUE, false))
				.collect(Collectors.toList()));
	}

	public String getTaxonId() {
		return taxonId;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public List<Reference<String>> getLociIds() {
		return lociIds;
	}

	public PrimaryKey getPrimaryKey() {
		return new PrimaryKey(taxonId, id);
	}

	public static class PrimaryKey {

		private String taxonId;
		private String id;

		public PrimaryKey(String taxonId, String id) {
			this.taxonId = taxonId;
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public String getTaxonId() {
			return taxonId;
		}

	}

}
