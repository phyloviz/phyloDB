package pt.ist.meic.phylodb.analysis.visualization.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label = "Coordinate")
public class Coordinate {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "x")
	private int x;
	@Property(name = "y")
	private int y;

	public Coordinate() {
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
