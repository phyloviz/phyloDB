package pt.ist.meic.phylodb.formatters.datasets;

import java.util.List;

public class FileDataset<T> {

	private List<T> entities;

	public FileDataset(List<T> entities) {
		this.entities = entities;
	}

	public List<T> getEntities() {
		return entities;
	}

}
