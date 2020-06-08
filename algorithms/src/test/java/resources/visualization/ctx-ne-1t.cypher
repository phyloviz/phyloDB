CREATE (p:Project {id: "project"}) WITH p
CREATE (p)-[:CONTAINS]->(d:Dataset {id: "dataset"}) WITH d
CREATE (d)-[:CONTAINS]->(p1:Profile {id: "1"}) WITH d, p1
CREATE (d)-[:CONTAINS]->(p2:Profile {id: "2"}) WITH d, p1, p2
CREATE (d)-[:CONTAINS]->(p3:Profile {id: "3"}) WITH d, p1, p2, p3
CREATE (d)-[:CONTAINS]->(p4:Profile {id: "4"}) WITH p1, p2, p3, p4
CREATE (p1)-[:DISTANCES {id: "inference", distance: 2}]->(p2) WITH p1, p2, p3, p4
CREATE (p2)-[:DISTANCES {id: "inference", distance: 3}]->(p3) WITH p1, p4
CREATE (p1)-[:DISTANCES {id: "inference", distance: 1}]->(p4)