package pt.ist.meic.phylodb.analysis.visualization.model;

import java.util.Arrays;
import java.util.Objects;

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

	public static class CoordinateOutputModel {

		private String profile_id;
		private double x;
		private double y;

		public CoordinateOutputModel() {
		}

		public CoordinateOutputModel(Coordinate coordinate) {
			this.profile_id = coordinate.getProfile().getId();
			this.x = coordinate.getX();
			this.y = coordinate.getY();
		}

		public String getProfile_id() {
			return profile_id;
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
			return x == that.x &&
					y == that.y &&
					Objects.equals(profile_id, that.profile_id);
		}

	}

}
