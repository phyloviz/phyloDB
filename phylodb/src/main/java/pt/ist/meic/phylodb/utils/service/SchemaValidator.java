package pt.ist.meic.phylodb.utils.service;

public interface SchemaValidator<E, S, U> {

	boolean test(E taxonId, S schemaId, U file);
}
