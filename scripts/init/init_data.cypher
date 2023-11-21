CALL apoc.cypher.runMany("MATCH(n) DETACH DELETE n;
CREATE (:User {provider: 'example', id: 'john.doe@example.net', deprecated: false})-[:CONTAINS_DETAILS {from: datetime(), version: 1}]->(:UserDetails {role: 'admin'})
CREATE (:User {provider: 'example', id: 'jane.doe@example.net', deprecated: false})-[:CONTAINS_DETAILS {from: datetime(), version: 1}]->(:UserDetails {role: 'admin'});", {});