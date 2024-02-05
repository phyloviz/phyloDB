import sys
import json

project_id = sys.argv[1]
taxon_id = sys.argv[2]
alleles_fasta_file = open(sys.argv[3], 'r')

# Parse FASTA file
alleles = []
for line in alleles_fasta_file:
  if line[0] == '>':
    (locus_id, allele_id) = line[1:].strip().split('_')
    allele = { 'projectId': project_id, 'taxonId': taxon_id, 'locusId': locus_id, 'id': allele_id, 'sequence': "" }
    alleles.append(allele)
  else:
    allele['sequence'] += line.strip()

# Generate CQL

print('WITH')
print('    ' + json.dumps(alleles).replace('\"projectId\"', 'projectId').replace('\"taxonId\"', 'taxonId').replace('\"locusId\"', 'locusId').replace('\"id\"', 'id').replace('\"sequence\"', 'sequence'))
print("    as argument")
print('UNWIND argument as param')
print('MATCH (t:Taxon {id: param.taxonId})-[:CONTAINS]->(l:Locus {id: param.locusId})')
print('WHERE t.deprecated = false AND l.deprecated = false')
print('CALL apoc.do.when(param.projectId IS NOT NULL,')
print('    "MATCH (p:Project {id: pid}) WHERE p.deprecated = false')
print('    MERGE (l)-[:CONTAINS]->(a:Allele {id: aid})<-[:CONTAINS]-(p) RETURN a as allele",')
print('    "OPTIONAL MATCH (l)-[:CONTAINS]->(a:Allele {id: aid}) WHERE NOT (a)<-[:CONTAINS]-(:Project)')
print('    CALL apoc.do.when(a IS NOT NULL, \'RETURN a as allele\', \'CREATE (l)-[:CONTAINS]->(n:Allele {id: aid}) RETURN n as allele\', {l: l, a: a, aid: aid})')
print('    YIELD value')
print('    RETURN value.allele as allele"')
print(', {l: l, pid: param.projectId, aid: param.id}) YIELD value as result')
print('WITH result.allele as a, param.sequence as sequence')
print('SET a.deprecated = false')
print('WITH a, sequence')
print('OPTIONAL MATCH (a)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)')
print('WHERE r.to IS NULL SET r.to = datetime()')
print('WITH a, sequence, COALESCE(MAX(r.version), 0) + 1 as v')
print('CREATE (a)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ad:AlleleDetails {sequence: sequence});')

