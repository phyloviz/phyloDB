package pt.ist.meic.phylodb.analysis.visualization.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * A GetVisualizationOutputModel is the output model representation of a {@link Visualization}
 * <p>
 * A GetVisualizationOutputModel is constituted by the {@link #project_id}, {@link #dataset_id}, {@link #inference_id}, and {@link #id} fields to identify the visualization.
 * It also contains the {@link #deprecated} field which indicates if the visualization is deprecated, and the {@link #algorithm} and {@link #coordinates}
 * which are the algorithm that was used to produce this visualization and the resultant coordinates, respectively.
 */
public class GetVisualizationOutputModel extends VisualizationOutputModel {

	private String algorithm;
	private CoordinateOutputModel[] coordinates;

	public GetVisualizationOutputModel() {
	}

	public GetVisualizationOutputModel(Visualization visualization) {
		super(visualization);
		this.algorithm = visualization.getAlgorithm().getName();
		this.coordinates = visualization.getCoordinates().stream()
				.map(CoordinateOutputModel::new)
				.toArray(CoordinateOutputModel[]::new);
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public CoordinateOutputModel[] getCoordinates() {
		return coordinates;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		GetVisualizationOutputModel that = (GetVisualizationOutputModel) o;
		return Objects.equals(algorithm, that.algorithm) &&
				Arrays.equals(coordinates, that.coordinates);
	}

	/**
	 * A CoordinateOutputModel is the output model representation of a {@link Coordinate}
	 * <p>
	 * A CoordinateOutputModel is constituted by the {@link #profile_id}, {@link #component} to specify the component of the visualization it belongs to,
	 * and by the {@link #x} and {@link #y} that are the axis positions.
	 */
	public static class CoordinateOutputModel {

		private String profile_id;
		private long component;
		private double x;
		private double y;

		public CoordinateOutputModel() {
		}

		public CoordinateOutputModel(Coordinate coordinate) {
			this.profile_id = coordinate.getProfile().getId();
			this.component = coordinate.getComponent();
			this.x = coordinate.getX();
			this.y = coordinate.getY();
		}

		public String getProfile_id() {
			return profile_id;
		}

		public long getComponent() {
			return component;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			CoordinateOutputModel that = (CoordinateOutputModel) o;
			return component == that.component &&
					Double.compare(that.x, x) == 0 &&
					Double.compare(that.y, y) == 0 &&
					Objects.equals(profile_id, that.profile_id);
		}

	}

}
