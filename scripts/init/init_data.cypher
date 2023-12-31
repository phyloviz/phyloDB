CALL apoc.cypher.runMany("MATCH(n) DETACH DELETE n;
CREATE (:User {provider: 'google', id: 'john.doe@gmail.com', deprecated: false})-[:CONTAINS_DETAILS {from: datetime(), version: 1}]->(:UserDetails {role: 'admin'})
CREATE (:User {provider: 'google', id: 'jane.doe@gmail.com', deprecated: false})-[:CONTAINS_DETAILS {from: datetime(), version: 1}]->(:UserDetails {role: 'admin'});", {});
