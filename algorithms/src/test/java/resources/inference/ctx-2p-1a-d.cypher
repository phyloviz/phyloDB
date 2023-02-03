CREATE (p:Project {id: "project"}) WITH p
CREATE (p)-[:CONTAINS]->(d:Dataset {id: "dataset"}) WITH d
CREATE (d)-[:CONTAINS]->(p:Profile {id: "1", deprecated: false}) WITH d, p
CREATE (p)-[:CONTAINS_DETAILS {version: 1}]->(:ProfileDetails)-[:HAS {part: 1, total: 1, version: 1}]->(:Allele {id: "1"}) WITH d
CREATE (d)-[:CONTAINS]->(p:Profile {id: "2", deprecated: false}) WITH d, p
CREATE (p)-[:CONTAINS_DETAILS {version: 2}]->(:ProfileDetails)-[:HAS {part: 1, total: 1, version: 1}]->(:Allele {id: "2"}) WITH d, p
CREATE (d)-[:CONTAINS]->(:Isolate {id: "isolate", deprecated: false})-[:CONTAINS_DETAILS {version: 1}]->(:IsolateDetails)-[:HAS]->(p) WITH d
CREATE (d)-[:CONTAINS]->(p:Profile {id: "3", deprecated: true}) WITH d, p
CREATE (p)-[:CONTAINS_DETAILS {version: 1}]->(:ProfileDetails)-[:HAS {part: 1, total: 1, version: 1}]->(:Allele {id: "3"}) WITH d, p
CREATE (d)-[:CONTAINS]->(:Isolate {id: "isolate", deprecated: false})-[:CONTAINS_DETAILS {version: 1}]->(:IsolateDetails)-[:HAS]->(p)