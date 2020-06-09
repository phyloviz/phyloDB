package pt.ist.meic.phylodb.job.model;

import pt.ist.meic.phylodb.analysis.Analysis;
import pt.ist.meic.phylodb.analysis.inference.model.InferenceAlgorithm;
import pt.ist.meic.phylodb.analysis.visualization.model.VisualizationAlgorithm;
import pt.ist.meic.phylodb.io.input.InputModel;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class JobInputModel implements InputModel<JobRequest> {

	public static final int INFERENCE_PARAMETERS_COUNT = 2, VISUALIZATION_PARAMETERS_COUNT = 2;

	private String analysis;
	private String algorithm;
	private Object[] parameters;

	public JobInputModel() {
	}

	public JobInputModel(String analysis, String algorithm, Object[] parameters) {
		this.analysis = analysis;
		this.algorithm = algorithm;
		this.parameters = parameters;
	}

	public String getAnalysis() {
		return analysis;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public Object[] getParameters() {
		return parameters;
	}

	@Override
	public Optional<JobRequest> toDomainEntity(String... params) {
		if(!Analysis.exists(analysis))
			return Optional.empty();
		JobRequest request;
		return (request = parseJobRequest(Analysis.valueOf(analysis.toUpperCase()), algorithm, parameters)) != null ?
				Optional.of(request) :
				Optional.empty();
	}

	private JobRequest parseJobRequest(Analysis analysis, String algorithm, Object[] parameters) {
		if(analysis == Analysis.INFERENCE)
			return parseInferenceJobRequest(algorithm, parameters);
		else if(analysis == Analysis.VISUALIZATION)
			return parseVisualizationJobRequest(algorithm, parameters);
		return null;
	}

	private JobRequest parseInferenceJobRequest(String algorithm, Object[] parameters) {
		if(!InferenceAlgorithm.exists(algorithm) || parameters == null || parameters.length != INFERENCE_PARAMETERS_COUNT || Arrays.stream(parameters).anyMatch(Objects::isNull))
			return null;
		return new JobRequest(Analysis.INFERENCE, algorithm, parameters);
	}

	private JobRequest parseVisualizationJobRequest(String algorithm, Object[] parameters) {
		if(!VisualizationAlgorithm.exists(algorithm) || parameters == null || parameters.length != VISUALIZATION_PARAMETERS_COUNT || Arrays.stream(parameters).anyMatch(Objects::isNull))
			return null;
		return new JobRequest(Analysis.VISUALIZATION, algorithm, parameters);
	}

}
