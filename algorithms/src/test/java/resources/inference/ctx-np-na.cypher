CREATE (p:Project {id: "project"}) WITH p
CREATE (p)-[:CONTAINS]->(d:Dataset {id: "dataset"}) WITH d
CREATE (d)-[:CONTAINS]->(p:Profile {id: "1"}) WITH d, p
CREATE (p)-[:CONTAINS_DETAILS {version: 1}]->(pd:ProfileDetails)-[:HAS {part: 1, total: 3, version: 1}]->(a11:Allele {id: "1"}) WITH d, pd, a11
CREATE (pd)-[:HAS {part: 2, total: 3, version: 1}]->(:Allele {id: "1"}) WITH d, pd, a11
CREATE (pd)-[:HAS {part: 3, total: 3, version: 1}]->(:Allele {id: "2"}) WITH d, pd, a11
CREATE (d)-[:CONTAINS]->(p:Profile {id: "2"}) WITH d, p, a11
CREATE (p)-[:CONTAINS_DETAILS {version: 1}]->(pd:ProfileDetails)-[:HAS {part: 1, total: 3, version: 1}]->(a11) WITH d, pd
CREATE (pd)-[:HAS {part: 2, total: 3, version: 1}]->(:Allele {id: "2"}) WITH d, pd
CREATE (pd)-[:HAS {part: 3, total: 3, version: 1}]->(a31:Allele {id: "1"}) WITH d, a31
CREATE (d)-[:CONTAINS]->(p:Profile {id: "3"}) WITH d, p, a31
CREATE (p)-[:CONTAINS_DETAILS {version: 1}]->(pd:ProfileDetails)-[:HAS {part: 1, total: 3, version: 1}]->(:Allele {id: "3"}) WITH pd, a31
CREATE (pd)-[:HAS {part: 2, total: 3, version: 1}]->(:Allele {id: "5"}) WITH pd, a31
CREATE (pd)-[:HAS {part: 3, total: 3, version: 1}]->(a31)