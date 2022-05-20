CALL apoc.schema.assert({Taxon:['id'], Locus:['id'], Allele:['id'], Schema:['id'], Project:['id'], Dataset:['id'], Profile:['id'], Isolate:['id']}, {}, true)
YIELD label, key, keys, unique, action
RETURN label, key, keys, unique, action;