package pt.ist.meic.phylodb.typing.schema.model;

import java.util.ArrayList;
import java.util.List;

public class Schema {

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
	private String[] lociIds;

	public Schema() {
	}

	public Schema(String taxonId, String id, String type, String description, String[] lociId) {
		this.taxonId = taxonId;
		this.id = id;
		this.type = type;
		this.description = description;
		this.lociIds = lociId;
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

	public String[] getLociIds() {
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
