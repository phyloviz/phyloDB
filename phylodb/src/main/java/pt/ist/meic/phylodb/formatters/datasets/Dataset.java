package pt.ist.meic.phylodb.formatters.datasets;

import java.util.List;

public class Dataset<T> {

	private List<T> entities;

	public Dataset(List<T> entities) {
		this.entities = entities;
	}

	public List<T> getEntities() {
		return entities;
	}

}
