#!/bin/bash

set -e

cd "$(dirname "$0")"

if [ -z "$USER_ID" ]; then cat << EOF
Error: USER_ID is not defined. Please follow these steps:

# Execute
#
# > export USER_ID="YOUR_USER_ID"
#
# where YOUR_USER_ID is the email address associated with the account used to
# create TOKEN.
EOF
  exit 1
else
  echo "ok USER_ID defined as ${USER_ID}"
fi

cat << EOF > init_data.cypher
CALL apoc.cypher.runMany("MATCH(n) DETACH DELETE n;
CREATE (:User {provider: 'google', id: '${USER_ID}', deprecated: false})-[:CONTAINS_DETAILS {from: datetime(), version: 1}]->(:UserDetails {role: 'admin'});", {});
EOF

#cat init_data.cypher | docker exec --interactive phylodb-neo4j sh -c "cypher-shell -u neo4j -p password"

