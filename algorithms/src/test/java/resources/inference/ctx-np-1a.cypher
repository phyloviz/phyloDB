CREATE (p:Project {id: "project"}) WITH p
CREATE (p)-[:CONTAINS]->(d:Dataset {id: "dataset"}) WITH d
CREATE (d)-[:CONTAINS]->(p:Profile {id: "1"})-[:HAS {part: 1, total: 1, version: 1}]->(a:Allele {id: "1"}) WITH d, p, a
CREATE (p)-[:CONTAINS_DETAILS {version: 1}]->(:ProfileDetails) WITH d, a
CREATE (d)-[:CONTAINS]->(p:Profile {id: "2"})-[:HAS {part: 1, total: 1, version: 1}]->(a) WITH d, p
CREATE (p)-[:CONTAINS_DETAILS {version: 2}]->(:ProfileDetails) WITH d
CREATE (d)-[:CONTAINS]->(p:Profile {id: "3"})-[:HAS {part: 1, total: 1, version: 1}]->(:Allele {id: "2"}) WITH d, p
CREATE (p)-[:CONTAINS_DETAILS {version: 3}]->(:ProfileDetails) WITH d, p
CREATE (d)-[:CONTAINS]->(:Isolate {id: "isolate"})-[:HAS]->(p)