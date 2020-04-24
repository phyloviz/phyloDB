package pt.ist.meic.phylodb.utils;

import org.neo4j.ogm.model.QueryStatistics;
import org.neo4j.ogm.model.Result;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class MockResult implements Result {
	@Override
	public QueryStatistics queryStatistics() {
		return new QueryStatistics() {
			@Override
			public boolean containsUpdates() {
				return false;
			}

			@Override
			public int getNodesCreated() {
				return 0;
			}

			@Override
			public int getNodesDeleted() {
				return 0;
			}

			@Override
			public int getPropertiesSet() {
				return 0;
			}

			@Override
			public int getRelationshipsCreated() {
				return 0;
			}

			@Override
			public int getRelationshipsDeleted() {
				return 0;
			}

			@Override
			public int getLabelsAdded() {
				return 0;
			}

			@Override
			public int getLabelsRemoved() {
				return 0;
			}

			@Override
			public int getIndexesAdded() {
				return 0;
			}

			@Override
			public int getIndexesRemoved() {
				return 0;
			}

			@Override
			public int getConstraintsAdded() {
				return 0;
			}

			@Override
			public int getConstraintsRemoved() {
				return 0;
			}
		};
	}

	@Override
	public Iterator<Map<String, Object>> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Iterable<Map<String, Object>> queryResults() {
		return null;
	}

}
