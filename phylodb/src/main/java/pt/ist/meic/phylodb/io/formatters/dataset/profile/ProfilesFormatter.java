package pt.ist.meic.phylodb.io.formatters.dataset.profile;

import pt.ist.meic.phylodb.io.formatters.Formatter;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.typing.Method;
import pt.ist.meic.phylodb.typing.profile.model.Profile;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;

public abstract class ProfilesFormatter extends Formatter<Profile> {

	protected UUID projectId;
	protected UUID datasetId;
	protected Schema schema;
	protected String missing;
	protected boolean authorized;

	public static ProfilesFormatter get(String format) {
		return new HashMap<String, ProfilesFormatter>() {{
			put(Method.MLST.getName(), new MlFormatter());
			put(Method.MLVA.getName(), new MlFormatter());
			put(Method.SNP.getName(), new SnpFormatter());
		}}.get(format);
	}

	protected static List<String> formatAlleles(List<Entity<Allele.PrimaryKey>> alleles) {
		List<String> output = new ArrayList<>();
		for (Entity<Allele.PrimaryKey> allele : alleles) {
			if(allele == null)
				output.add(" ");
			else
				output.add(allele.getPrimaryKey().getId());
		}
		return output;
	}

	@Override
	protected boolean init(Iterator<String> it, Object... params) {
		this.projectId = (UUID) params[0];
		this.datasetId = (UUID) params[1];
		this.schema = (Schema) params[2];
		this.missing = (String) params[3];
		this.authorized = (boolean) params[4];
		return true;
	}

}
