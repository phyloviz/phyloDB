#Check user:
=====================================
Query:OPTIONAL MATCH (u:User {id: $1, provider: $2})
RETURN COALESCE(u.deprecated = false, false);
Parameters: 1 -> aplf@tecnico.pt
Parameters: 2 -> google
=====================================

#Check permissions:
=====================================
Query:MATCH (u:User {id: $1, provider: $2})-[r:CONTAINS_DETAILS]->(ud:UserDetails)
WHERE r.to IS NULL
RETURN u.id as id, u.provider as provider, u.deprecated as deprecated, r.version as version,
ud.role as role;
Parameters: 1 -> aplf@tecnico.pt
Parameters: 2 -> google
Parameters: 3 -> -1
=====================================

#Get users:
=====================================
Query:MATCH (u:User)-[r:CONTAINS_DETAILS]->(ud:UserDetails)
WHERE u.deprecated = false AND r.to IS NULL
RETURN u.id as id, u.provider as provider, u.deprecated as deprecated, r.version as version
ORDER BY size(u.id), u.id, size(u.provider), u.provider SKIP $1 LIMIT $2;
Parameters: 1 -> 0
Parameters: 2 -> 500
=====================================

Get taxa:
=====================================
Query:MATCH (t:Taxon)-[r:CONTAINS_DETAILS]->(td:TaxonDetails)
WHERE t.deprecated = false AND r.to IS NULL
RETURN t.id as id, t.deprecated as deprecated, r.version as version
ORDER BY size(t.id), t.id SKIP $1 LIMIT $2;
Parameters: 1 -> 0
Parameters: 2 -> 500
=====================================

Create taxon:
=====================================
Query:MERGE (t:Taxon {id: $1}) SET t.deprecated = false WITH t
OPTIONAL MATCH (t)-[r:CONTAINS_DETAILS]->(td:TaxonDetails)
WHERE r.to IS NULL SET r.to = datetime()
WITH t, COALESCE(MAX(r.version), 0) + 1 as v
CREATE (t)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(td:TaxonDetails {description: $2});
Parameters: 1 -> bbacilliformis
Parameters: 2 -> Example taxon
=====================================

Get loci:
=====================================
Query:MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus)-[r:CONTAINS_DETAILS]->(ld:LocusDetails)
WHERE t.deprecated = false AND l.deprecated = false AND r.to IS NULL
RETURN t.id as taxonId, l.id as id, l.deprecated as deprecated, r.version as version
ORDER BY t.id, size(l.id), l.id SKIP $2 LIMIT $3;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> 0
Parameters: 3 -> 500
=====================================

Create locus:
=====================================
Query:OPTIONAL MATCH (t:Taxon {id: $1})
RETURN COALESCE(t.deprecated = false, false);
Parameters: 1 -> bbacilliformis
=====================================
Query:MATCH (t:Taxon {id: $1})
WHERE t.deprecated = false
MERGE (t)-[:CONTAINS]->(l:Locus {id: $2}) SET l.deprecated = false WITH l
OPTIONAL MATCH (l)-[r:CONTAINS_DETAILS]->(ld:LocusDetails)
WHERE r.to IS NULL SET r.to = datetime()
WITH l, COALESCE(MAX(r.version), 0) + 1 as v
CREATE (l)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ld:LocusDetails {description: $3});
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus7
Parameters: 3 -> Example locus
=====================================

Create alleles:
=====================================
Query:OPTIONAL MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus {id: $2})
RETURN COALESCE(l.deprecated = false, false);
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus7
=====================================
Query:OPTIONAL MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus {id: $2})-[:CONTAINS]->(a:Allele {id: $3})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN COALESCE(a.deprecated = false, false);
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus7
Parameters: 3 -> 1
Parameters: 4 -> null
=====================================
Query:OPTIONAL MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus {id: $2})-[:CONTAINS]->(a:Allele {id: $3})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN COALESCE(a.deprecated = false, false);
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus7
Parameters: 3 -> 10
Parameters: 4 -> null
=====================================
Query:OPTIONAL MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus {id: $2})-[:CONTAINS]->(a:Allele {id: $3})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN COALESCE(a.deprecated = false, false);
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus7
Parameters: 3 -> 14
Parameters: 4 -> null
=====================================
Query:OPTIONAL MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus {id: $2})-[:CONTAINS]->(a:Allele {id: $3})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN COALESCE(a.deprecated = false, false);
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus7
Parameters: 3 -> 18
Parameters: 4 -> null
=====================================
Query:OPTIONAL MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus {id: $2})-[:CONTAINS]->(a:Allele {id: $3})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN COALESCE(a.deprecated = false, false);
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus7
Parameters: 3 -> 49
Parameters: 4 -> null
=====================================
Query:OPTIONAL MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus {id: $2})-[:CONTAINS]->(a:Allele {id: $3})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN COALESCE(a.deprecated = false, false);
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus7
Parameters: 3 -> 51
Parameters: 4 -> null
=====================================
Query:OPTIONAL MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus {id: $2})-[:CONTAINS]->(a:Allele {id: $3})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN COALESCE(a.deprecated = false, false);
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus7
Parameters: 3 -> 8
Parameters: 4 -> null
=====================================
Query:UNWIND $1 as param
MATCH (t:Taxon {id: param.taxonId})-[:CONTAINS]->(l:Locus {id: param.locusId})
WHERE t.deprecated = false AND l.deprecated = false
CALL apoc.do.when(param.projectId IS NOT NULL,
    "MATCH (p:Project {id: pid}) WHERE p.deprecated = false
    MERGE (l)-[:CONTAINS]->(a:Allele {id: aid})<-[:CONTAINS]-(p) RETURN a as allele",
    "OPTIONAL MATCH (l)-[:CONTAINS]->(a:Allele {id: aid}) WHERE NOT (a)<-[:CONTAINS]-(:Project)
    CALL apoc.do.when(a IS NOT NULL, 'RETURN a as allele', 'CREATE (l)-[:CONTAINS]->(n:Allele {id: aid}) RETURN n as allele', {l: l, a: a, aid: aid})
    YIELD value
    RETURN value.allele as allele"
, {l: l, pid: param.projectId, aid: param.id}) YIELD value as result
WITH result.allele as a, param.sequence as sequence
SET a.deprecated = false
WITH a, sequence
OPTIONAL MATCH (a)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)
WHERE r.to IS NULL SET r.to = datetime()
WITH a, sequence, COALESCE(MAX(r.version), 0) + 1 as v
CREATE (a)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ad:AlleleDetails {sequence: sequence});
Parameters: 1 -> [Ljava.lang.Object;@6a193aaa
=====================================

Get alleles:
=====================================
Query:MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus {id: $2})-[:CONTAINS]->(a:Allele)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)
WHERE t.deprecated = false AND l.deprecated = false AND a.deprecated = false AND r.to IS NULL AND NOT (a)<-[:CONTAINS]-(:Project)

RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version
ORDER BY t.id, l.id, size(a.id), a.id SKIP $3 LIMIT $4;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus7
Parameters: 3 -> 0
Parameters: 4 -> 500
=====================================

Get allele:
=====================================
Query:MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus {id: $2})-[:CONTAINS]->(a:Allele {id: $3})-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)
WHERE r.to IS NULL AND NOT (a)<-[:CONTAINS]-(:Project)
RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, ad.sequence as sequence
;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus1
Parameters: 3 -> 1
Parameters: 4 -> -1
Parameters: 5 -> null
=====================================

Get schemas:
=====================================
Query:MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $2})
WHERE r.to IS NULL WITH t, s, r, sd, l, h
ORDER BY h.part
WITH t, s, r, sd, collect(DISTINCT {id: l.id, deprecated: l.deprecated, version: h.version}) as lociIds
RETURN t.id as taxonId, s.id as id, s.type as type, s.deprecated as deprecated, r.version as version, sd.description as description, lociIds;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> mlst7
Parameters: 3 -> -1
=====================================

Create schema:
=====================================
Query:MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus)<-[:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {type: $2})
WHERE s.deprecated = false AND r.to IS NULL
WITH t, s, r, sd
MATCH (sd)-[:HAS {part: 1}]->(l0:Locus {id: $3}) WITH t, s, r, sd
MATCH (sd)-[:HAS {part: 2}]->(l1:Locus {id: $4}) WITH t, s, r, sd
MATCH (sd)-[:HAS {part: 3}]->(l2:Locus {id: $5}) WITH t, s, r, sd
MATCH (sd)-[:HAS {part: 4}]->(l3:Locus {id: $6}) WITH t, s, r, sd
MATCH (sd)-[:HAS {part: 5}]->(l4:Locus {id: $7}) WITH t, s, r, sd
MATCH (sd)-[:HAS {part: 6}]->(l5:Locus {id: $8}) WITH t, s, r, sd
MATCH (sd)-[:HAS {part: 7}]->(l6:Locus {id: $9}) WITH t, s, r, sd
MATCH (sd)-[h:HAS]->(l:Locus) WITH t, s, r, sd, h, l
ORDER BY h.part
WITH t, s, r, sd, collect(DISTINCT {id: l.id, deprecated: l.deprecated, version: h.version}) as lociIds
RETURN t.id as taxonId, s.id as id, s.deprecated as deprecated, r.version as version,
s.type as type, sd.description as description, lociIds;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> mlst
Parameters: 3 -> locus1
Parameters: 4 -> locus2
Parameters: 5 -> locus3
Parameters: 6 -> locus4
Parameters: 7 -> locus5
Parameters: 8 -> locus6
Parameters: 9 -> locus7
=====================================
Query:MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus)
WHERE t.deprecated = false AND l.deprecated = false AND l.id IN [$2,$3,$4,$5,$6,$7,$8]
RETURN COUNT(l.id);
Parameters: 1 -> bbacilliformis
Parameters: 2 -> locus1
Parameters: 3 -> locus2
Parameters: 4 -> locus3
Parameters: 5 -> locus4
Parameters: 6 -> locus5
Parameters: 7 -> locus6
Parameters: 8 -> locus7
=====================================
Query:OPTIONAL MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $2})
WITH s, collect(l) as loci
RETURN COALESCE(s.deprecated = false, false);
Parameters: 1 -> bbacilliformis
Parameters: 2 -> mlst7
=====================================
Query:CREATE (s:Schema {id: $1, type: $2, deprecated: false})-[:CONTAINS_DETAILS {from: datetime(), version: 1}]->(sd:SchemaDetails {description: $3}) WITH sd
 MATCH (t:Taxon {id: $4}) WHERE t.deprecated = false
WITH t, sd
UNWIND $5 as param
MATCH (t)-[:CONTAINS]->(l:Locus {id: param.id})-[r:CONTAINS_DETAILS]->(:LocusDetails)
WHERE l.deprecated = false AND r.to IS NULL
CREATE (sd)-[:HAS {part: param.part, version: r.version}]->(l);
Parameters: 1 -> mlst7
Parameters: 2 -> mlst
Parameters: 3 -> demo 7 loci schema
Parameters: 4 -> bbacilliformis
Parameters: 5 -> [Ljava.lang.Object;@1f3d1c38
=====================================

Get projects:
=====================================
Query:MATCH (p:Project)-[r:CONTAINS_DETAILS]->(pd:ProjectDetails)
WHERE p.deprecated = false AND r.to IS NULL
OPTIONAL MATCH (pd)-[:HAS]->(u:User)
WITH p, r, pd, collect(DISTINCT {id: u.id, provider: u.provider}) as users
WHERE {id: $1, provider: $2} IN users OR pd.type = "public"
RETURN p.id as id, p.deprecated as deprecated, r.version as version
ORDER BY size(p.id), p.id SKIP $3 LIMIT $4;
Parameters: 1 -> aplf@tecnico.pt
Parameters: 2 -> google
Parameters: 3 -> 0
Parameters: 4 -> 500
=====================================

Create project:
=====================================
Query:MERGE (p:Project {id: $1}) SET p.deprecated = false WITH p
OPTIONAL MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProjectDetails)
WHERE r.to IS NULL SET r.to = datetime()
WITH p, COALESCE(r.version, 0) + 1 as v
CREATE (p)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(pd:ProjectDetails {name: $2, type: $3, description: $4})
WITH pd
UNWIND $5 as param
MATCH (u:User {id: param.id, provider: param.provider}) WHERE u.deprecated = false CREATE (pd)-[:HAS]->(u);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> Test
Parameters: 3 -> private
Parameters: 4 -> Test project
Parameters: 5 -> java.util.stream.ReferencePipeline$3@6acf2f16
=====================================
Query:MATCH (p:Project)-[r:CONTAINS_DETAILS]->(pd:ProjectDetails)
WHERE p.deprecated = false AND r.to IS NULL
OPTIONAL MATCH (pd)-[:HAS]->(u:User)
WITH p, r, pd, collect(DISTINCT {id: u.id, provider: u.provider}) as users
WHERE {id: $1, provider: $2} IN users OR pd.type = "public"
RETURN p.id as id, p.deprecated as deprecated, r.version as version
ORDER BY size(p.id), p.id SKIP $3 LIMIT $4;
Parameters: 1 -> aplf@tecnico.pt
Parameters: 2 -> google
Parameters: 3 -> 0
Parameters: 4 -> 500
=====================================

Get datasets:
=====================================
Query:MATCH (p:Project {id: $1})-[:CONTAINS]->(d:Dataset)-[r:CONTAINS_DETAILS]->(dd:DatasetDetails)
WHERE p.deprecated = false AND d.deprecated = false AND r.to IS NULL
RETURN p.id as projectId, d.id as datasetId, d.deprecated as deprecated, r.version as version
ORDER BY p.id, size(d.id), d.id SKIP $2 LIMIT $3;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 0
Parameters: 3 -> 500
=====================================

Create dataset:
=====================================
Query:OPTIONAL MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $2})
WITH s, collect(l) as loci
RETURN COALESCE(s.deprecated = false, false);
Parameters: 1 -> bbacilliformis
Parameters: 2 -> mlst7
=====================================
Query:MATCH (p:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[r1:CONTAINS_DETAILS]->(dd:DatasetDetails)-[h:HAS]->(s:Schema)-[r2:CONTAINS_DETAILS]->(sd:SchemaDetails)
WHERE r2.version = h.version AND r1.to IS NULL
MATCH (sd)-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon)
WITH p, d, r1, dd, h, s, t, collect(l) as loci
RETURN p.id as projectId, d.id as datasetId, d.deprecated as deprecated, r1.version as version, dd.description as description, t.id as taxonId, s.id as schemaId, h.version as schemaVersion, s.deprecated as schemaDeprecated;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> -1
=====================================
Query:MATCH (p:Project {id: $1})
MERGE (p)-[:CONTAINS]->(d:Dataset {id : $2}) SET d.deprecated = false WITH d
OPTIONAL MATCH (d)-[r:CONTAINS_DETAILS]->(dd:DatasetDetails)WHERE r.to IS NULL SET r.to = datetime()
WITH d, COALESCE(MAX(r.version), 0) + 1 as v
CREATE (d)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(dd:DatasetDetails {description: $3}) WITH dd
MATCH (s:Schema {id: $4})-[r:CONTAINS_DETAILS]->(sd:SchemaDetails)-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon {id: $5})
WHERE r.to IS NULL
WITH dd, s, r, collect(l) as loci
CREATE (dd)-[:HAS {version: r.version}]->(s);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> Example dataset
Parameters: 4 -> mlst7
Parameters: 5 -> bbacilliformis
=====================================
Query:MATCH (p:Project {id: $1})-[:CONTAINS]->(d:Dataset)-[r:CONTAINS_DETAILS]->(dd:DatasetDetails)
WHERE p.deprecated = false AND d.deprecated = false AND r.to IS NULL
RETURN p.id as projectId, d.id as datasetId, d.deprecated as deprecated, r.version as version
ORDER BY p.id, size(d.id), d.id SKIP $2 LIMIT $3;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 0
Parameters: 3 -> 500
=====================================

Create profiles:
=====================================
Query:MATCH (p:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[r1:CONTAINS_DETAILS]->(dd:DatasetDetails)-[h:HAS]->(s:Schema)-[r2:CONTAINS_DETAILS]->(sd:SchemaDetails)
WHERE r2.version = h.version AND r1.to IS NULL
MATCH (sd)-[:HAS]->(l:Locus)<-[:CONTAINS]-(t:Taxon)
WITH p, d, r1, dd, h, s, t, collect(l) as loci
RETURN p.id as projectId, d.id as datasetId, d.deprecated as deprecated, r1.version as version, dd.description as description, t.id as taxonId, s.id as schemaId, h.version as schemaVersion, s.deprecated as schemaDeprecated;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> -1
=====================================
Query:MATCH (t:Taxon {id: $1})-[:CONTAINS]->(l:Locus)<-[h:HAS]-(sd:SchemaDetails)<-[r:CONTAINS_DETAILS]-(s:Schema {id: $2})
WHERE r.version = $3 WITH t, s, r, sd, l, h
ORDER BY h.part
WITH t, s, r, sd, collect(DISTINCT {id: l.id, deprecated: l.deprecated, version: h.version}) as lociIds
RETURN t.id as taxonId, s.id as id, s.type as type, s.deprecated as deprecated, r.version as version, sd.description as description, lociIds;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> mlst7
Parameters: 3 -> 1
=====================================
Query:OPTIONAL MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
RETURN COALESCE(p.deprecated = false, false);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 1
=====================================
Query:MATCH (t:Taxon {id: $1}) UNWIND $2 as param
CALL apoc.when(param.project IS NOT NULL,
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE (a)<-[:CONTAINS]-(:Project {id: param.project})
RETURN a.id as result",
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN a.id as result",
{t: t, param: param}) YIELD value RETURN value.result as present;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> [Ljava.lang.Object;@6b5c4b1f
=====================================
Query:OPTIONAL MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
RETURN COALESCE(p.deprecated = false, false);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 2
=====================================
Query:MATCH (t:Taxon {id: $1}) UNWIND $2 as param
CALL apoc.when(param.project IS NOT NULL,
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE (a)<-[:CONTAINS]-(:Project {id: param.project})
RETURN a.id as result",
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN a.id as result",
{t: t, param: param}) YIELD value RETURN value.result as present;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> [Ljava.lang.Object;@4244135a
=====================================
Query:OPTIONAL MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
RETURN COALESCE(p.deprecated = false, false);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 3
=====================================
Query:MATCH (t:Taxon {id: $1}) UNWIND $2 as param
CALL apoc.when(param.project IS NOT NULL,
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE (a)<-[:CONTAINS]-(:Project {id: param.project})
RETURN a.id as result",
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN a.id as result",
{t: t, param: param}) YIELD value RETURN value.result as present;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> [Ljava.lang.Object;@4bc9bcbe
=====================================
Query:OPTIONAL MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
RETURN COALESCE(p.deprecated = false, false);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 4
=====================================
Query:MATCH (t:Taxon {id: $1}) UNWIND $2 as param
CALL apoc.when(param.project IS NOT NULL,
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE (a)<-[:CONTAINS]-(:Project {id: param.project})
RETURN a.id as result",
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN a.id as result",
{t: t, param: param}) YIELD value RETURN value.result as present;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> [Ljava.lang.Object;@61f67925
=====================================
Query:OPTIONAL MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
RETURN COALESCE(p.deprecated = false, false);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 5
=====================================
Query:MATCH (t:Taxon {id: $1}) UNWIND $2 as param
CALL apoc.when(param.project IS NOT NULL,
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE (a)<-[:CONTAINS]-(:Project {id: param.project})
RETURN a.id as result",
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN a.id as result",
{t: t, param: param}) YIELD value RETURN value.result as present;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> [Ljava.lang.Object;@2ccc41be
=====================================
Query:OPTIONAL MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
RETURN COALESCE(p.deprecated = false, false);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 6
=====================================
Query:MATCH (t:Taxon {id: $1}) UNWIND $2 as param
CALL apoc.when(param.project IS NOT NULL,
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE (a)<-[:CONTAINS]-(:Project {id: param.project})
RETURN a.id as result",
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN a.id as result",
{t: t, param: param}) YIELD value RETURN value.result as present;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> [Ljava.lang.Object;@4ef059be
=====================================
Query:OPTIONAL MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
RETURN COALESCE(p.deprecated = false, false);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 7
=====================================
Query:MATCH (t:Taxon {id: $1}) UNWIND $2 as param
CALL apoc.when(param.project IS NOT NULL,
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE (a)<-[:CONTAINS]-(:Project {id: param.project})
RETURN a.id as result",
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN a.id as result",
{t: t, param: param}) YIELD value RETURN value.result as present;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> [Ljava.lang.Object;@48da2092
=====================================
Query:OPTIONAL MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
RETURN COALESCE(p.deprecated = false, false);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 8
=====================================
Query:MATCH (t:Taxon {id: $1}) UNWIND $2 as param
CALL apoc.when(param.project IS NOT NULL,
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE (a)<-[:CONTAINS]-(:Project {id: param.project})
RETURN a.id as result",
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN a.id as result",
{t: t, param: param}) YIELD value RETURN value.result as present;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> [Ljava.lang.Object;@1a3757fd
=====================================
Query:OPTIONAL MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
RETURN COALESCE(p.deprecated = false, false);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 9
=====================================
Query:MATCH (t:Taxon {id: $1}) UNWIND $2 as param
CALL apoc.when(param.project IS NOT NULL,
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE (a)<-[:CONTAINS]-(:Project {id: param.project})
RETURN a.id as result",
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN a.id as result",
{t: t, param: param}) YIELD value RETURN value.result as present;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> [Ljava.lang.Object;@54b824b4
=====================================
Query:OPTIONAL MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
RETURN COALESCE(p.deprecated = false, false);
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 10
=====================================
Query:MATCH (t:Taxon {id: $1}) UNWIND $2 as param
CALL apoc.when(param.project IS NOT NULL,
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE (a)<-[:CONTAINS]-(:Project {id: param.project})
RETURN a.id as result",
"OPTIONAL MATCH (t)-[:CONTAINS]->(l:Locus {id: param.locus})-[:CONTAINS]->(a:Allele {id: param.allele})
WHERE NOT (a)<-[:CONTAINS]-(:Project)
RETURN a.id as result",
{t: t, param: param}) YIELD value RETURN value.result as present;
Parameters: 1 -> bbacilliformis
Parameters: 2 -> [Ljava.lang.Object;@18c9ba34
=====================================
Query:UNWIND $1 as param
MATCH (pj:Project {id: param.projectId})-[:CONTAINS]->(d:Dataset {id: param.datasetId})
WHERE d.deprecated = false
MERGE (d)-[:CONTAINS]->(p:Profile {id: param.id}) SET p.deprecated = false WITH param, pj, d, p
OPTIONAL MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)
WHERE r.to IS NULL SET r.to = datetime()
WITH param, pj, d, p, COALESCE(r.version, 0) + 1 as v
CREATE (p)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(pd:ProfileDetails {aka: param.aka})
WITH param, pj, d, pd
MATCH (d)-[r1:CONTAINS_DETAILS]->(dd:DatasetDetails)-[h:HAS]->(s:Schema)-[r2:CONTAINS_DETAILS]->(sd:SchemaDetails)
WHERE r1.to IS NULL AND r2.version = h.version
UNWIND param.alleles as n
MATCH (sd)-[:HAS {part: n.part}]->(l:Locus)
CALL apoc.do.when(param.project = TRUE,
    "MATCH (l)-[:CONTAINS]->(a:Allele {id: n.id})-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)
    WHERE r.to IS NULL AND (a)<-[:CONTAINS]-(pj)
    CREATE (pd)-[:HAS {version: r.version, part: n.part, total: n.total}]->(a)    RETURN TRUE",
    "MATCH (l)-[:CONTAINS]->(a:Allele {id: n.id})-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)
    WHERE r.to IS NULL AND NOT (a)<-[:CONTAINS]-(:Project)
    CREATE (pd)-[:HAS {version: r.version, part: n.part, total: n.total}]->(a)
    RETURN TRUE"
, {l: l, pd: pd, n: n, pj: pj}) YIELD value
RETURN 0;
Parameters: 1 -> [Ljava.lang.Object;@115b67
=====================================

List profiles:
=====================================
Query:MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)
WHERE p.deprecated = false AND r.to IS NULL
RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated
ORDER BY pj.id, d.id, size(p.id), p.id SKIP $3 LIMIT $4;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 0
Parameters: 4 -> 500
=====================================

Get profile:
=====================================
Query:MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)
WHERE r.to IS NULL
OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)
RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,
pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 1
Parameters: 4 -> -1
=====================================
Query:MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)
WHERE r.to IS NULL
OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)
RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,
pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 2
Parameters: 4 -> -1
=====================================
Query:MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)
WHERE r.to IS NULL
OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)
RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,
pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 3
Parameters: 4 -> -1
=====================================
Query:MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)
WHERE r.to IS NULL
OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)
RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,
pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 4
Parameters: 4 -> -1
=====================================
Query:MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)
WHERE r.to IS NULL
OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)
RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,
pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 5
Parameters: 4 -> -1
=====================================
Query:MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)
WHERE r.to IS NULL
OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)
RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,
pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 6
Parameters: 4 -> -1
=====================================
Query:MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)
WHERE r.to IS NULL
OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)
RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,
pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 7
Parameters: 4 -> -1
=====================================
Query:MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)
WHERE r.to IS NULL
OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)
RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,
pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 8
Parameters: 4 -> -1
=====================================
Query:MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)
WHERE r.to IS NULL
OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)
RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,
pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 9
Parameters: 4 -> -1
=====================================
Query:MATCH (pj:Project {id: $1})-[:CONTAINS]->(d:Dataset {id: $2})-[:CONTAINS]->(p:Profile {id: $3})
MATCH (p)-[r:CONTAINS_DETAILS]->(pd:ProfileDetails)-[h:HAS]->(a:Allele)<-[:CONTAINS]-(l:Locus)<-[:CONTAINS]-(t:Taxon)
WHERE r.to IS NULL
OPTIONAL MATCH (a)<-[:CONTAINS]-(pj2:Project)
RETURN pj.id as projectId, d.id as datasetId, p.id as id, r.version as version, p.deprecated as deprecated,
pd.aka as aka, collect(DISTINCT {project: pj2.id, taxon: t.id, locus: l.id, id: a.id, version: h.version, deprecated: a.deprecated, part:h.part, total: h.total}) as alleles;
Parameters: 1 -> c4eaa96c-bed7-4d47-a9b4-792b5abc2212
Parameters: 2 -> 57f16aa4-23e4-4a61-84d0-a8a08553f9c9
Parameters: 3 -> 10
Parameters: 4 -> -1
=====================================

