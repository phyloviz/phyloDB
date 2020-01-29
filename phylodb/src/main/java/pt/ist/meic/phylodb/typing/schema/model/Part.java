package pt.ist.meic.phylodb.typing.schema.model;

import org.neo4j.ogm.annotation.*;
import pt.ist.meic.phylodb.phylogeny.locus.model.Locus;

@RelationshipEntity(type = "USES_LOCUS")
public class Part {

	@Id
	@GeneratedValue
	private Long relationshipId;

	@Property(name="part")
	private String number;

	@StartNode
	private Schema from;
	@EndNode
	private Locus to;

	public Part() {
	}

	public String getNumber() {
		return number;
	}

	public Schema getFrom() {
		return from;
	}

	public Locus getTo() {
		return to;
	}
}
