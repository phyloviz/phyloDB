CALL apoc.cypher.runMany("MATCH(n) DETACH DELETE n;
CREATE (:User {provider: 'google', id: 'admin_id', deprecated: false})-[:CONTAINS_DETAILS]->(:UserDetails {role: 'admin'});", {})