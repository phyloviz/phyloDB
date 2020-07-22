package pt.ist.meic.phylodb.job.model;

import pt.ist.meic.phylodb.analysis.Analysis;

/**
 * A JobRequest allows the jobs input model to be parsed into a domain object
 */
public class JobRequest {

	private final Analysis type;
	private final String algorithm;
	private final Object[] parameters;

	public JobRequest(Analysis type, String algorithm, Object[] parameters) {
		this.type = type;
		this.algorithm = algorithm;
		this.parameters = parameters;
	}

	public Analysis getType() {
		return type;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public Object[] getParameters() {
		return parameters;
	}

}
