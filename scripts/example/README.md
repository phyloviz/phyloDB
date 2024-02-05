# Examples

## Interaction with the API

The script [`example.sh`](scripts/example/example.sh) illustrates the API use cases.
It is important that a Google account is registered as admin, and that you have a valid token.
Check the initialization step in the [`example.sh`](scripts/example/example.sh) script, and/or the [template for data initialization](scripts/init/init_data.cypher).

## Data loading

Although data can be loaded through the API, in particular through `files` endpoints, it can be useful to load data directly into Neo4j.
We provide two scripts to convert alleles and profiles data to cypher queries, respectively.
Note that the following examples assume that we have a project, a dataset, a taxon, a schema, etc., like illustrated in the previous example.
Note also that the queries below do not perform safety checks as is the case of API.

Given a FASTA file with data for alleles, it can converted to a cypher query as follows:
```
python3 alleles2cql.py c4eaa96c-bed7-4d47-a9b4-792b5abc2212 bbacilliformis profiles_2.txt > input.cql
```
where `c4eaa96c-bed7-4d47-a9b4-792b5abc2212` is the project id, `bbacilliformis` is the taxon id, and `profiles_2.txt` is a FASTA file with alleles data.

Given a CSV file with data for profiles, it can converted to a cypher query as follows:
```
python3 profiles2cql.py c4eaa96c-bed7-4d47-a9b4-792b5abc2212 57f16aa4-23e4-4a61-84d0-a8a08553f9c9 profiles.txt > input.cql
```
where `c4eaa96c-bed7-4d47-a9b4-792b5abc2212` is the project id, `57f16aa4-23e4-4a61-84d0-a8a08553f9c9` is the dataset id, and `profiles.txt` is a CSV file with profiles data.

Queries can be submitted to Neo4j as follows:
```
cat input.cql | docker exec --interactive phylodb-neo4j sh -c "cypher-shell -u neo4j -p password"
```
where we are assuming that you have followed the setup instructions for phyloDB.

