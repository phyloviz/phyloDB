import sys
import json

project_id = sys.argv[1]
dataset_id = sys.argv[2]
profiles_file = open(sys.argv[3], 'r')

# Remove header
line = profiles_file.readline()

# Parse TSV file
profiles = []
for line in profiles_file:
    profile_token = line.strip().split('\t')

    profile = { 'project': False, 'projectId': project_id, 'datasetId': dataset_id, 'id': profile_token[0], 'aka': "", 'alleles': [] }
    total = len(profile_token) - 1
    for i in range(1, len(profile_token)):
        allele = { 'part': i, 'id': profile_token[i], 'total': total }
        profile['alleles'].append(allele)

    profiles.append(profile)

# Generate CQL
print('WITH')
print('    ' + json.dumps(profiles).replace('\"project\"', 'project').replace('\"projectId\"', 'projectId').replace('\"datasetId\"', 'datasetId').replace('\"id\"', 'id').replace('\"alleles\"', 'alleles').replace('\"aka\"', 'aka').replace('\"part\"', 'part').replace('\"total\"', 'total'))
print("    as argument")
print('UNWIND argument as param')
print('MATCH (pj:Project {id: param.projectId})-[:CONTAINS]->(d:Dataset {id: param.datasetId})')
print('WHERE d.deprecated = false')
print('MERGE (d)-[:CONTAINS]->(p:Profile {id: param.id}) SET p.deprecated = false WITH param, pj, d, p')
print('OPTIONAL MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)')
print('WHERE r.to IS NULL SET r.to = datetime()')
print('WITH param, pj, d, p, COALESCE(r.version, 0) + 1 as v')
print('CREATE (p)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(pd:ProfileDetails {aka: param.aka})')
print('WITH param, pj, d, pd')
print('MATCH (d)-[r1:CONTAINS_DETAILS]->(dd:DatasetDetails)-[h:HAS]->(s:Schema)-[r2:CONTAINS_DETAILS]->(sd:SchemaDetails)')
print('WHERE r1.to IS NULL AND r2.version = h.version')
print('UNWIND param.alleles as n')
print('MATCH (sd)-[:HAS {part: n.part}]->(l:Locus)')
print('CALL apoc.do.when(param.project = TRUE,')
print('    "MATCH (l)-[:CONTAINS]->(a:Allele {id: n.id})-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)')
print('    WHERE r.to IS NULL AND (a)<-[:CONTAINS]-(pj)')
print('    CREATE (pd)-[:HAS {version: r.version, part: n.part, total: n.total}]->(a)    RETURN TRUE",')
print('    "MATCH (l)-[:CONTAINS]->(a:Allele {id: n.id})-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)')
print('    WHERE r.to IS NULL AND NOT (a)<-[:CONTAINS]-(:Project)')
print('    CREATE (pd)-[:HAS {version: r.version, part: n.part, total: n.total}]->(a)')
print('    RETURN TRUE"')
print(', {l: l, pd: pd, n: n, pj: pj}) YIELD value')
print('RETURN 0;')
