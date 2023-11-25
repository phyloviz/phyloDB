# About phyloDB
This project provides a framework for large scale phylogenetic analysis in the form of a Web API, a graph oriented database ([Neo4j](https://neo4j.com/)), and a plugin for the latter. The goal of this project is to allow the representation of large phylogenetic networks and trees, as well as ancillary data, support queries on such data, and the deployment of algorithms for inferring/detecting patterns and for computing visualizations. It started being developed in the scope of a master thesis at IST (Instituto Superior Técnico). The unit tests and benchmarks developed are available in the [test folder](https://github.com/phyloviz/phyloDB/tree/master/phylodb/src/test/java/pt/ist/meic/phylodb).


# Requirements

The following software tools must be available in your system as they will be called by our scripts.
- Java Development Kit 8 or greater (`java` and `javac`)
- [Maven](https://maven.apache.org/install.html) (`mvn`)
- [Gradle](https://gradle.org/install/) (`gradle`)
- [Docker](https://www.docker.com/get-started/) (`docker`, `docker-compose` and preferably the `docker buildx` command)

Configuration scripts will check if these are available.

# Configuration

For Linux/UNIX systems, please ensure that a `docker` user group exists in your system and that the executing user belongs to it.

## Building the JAR files, obtaining Neo4j extensions and building the Docker image

### Cloning this repository

    git clone https://github.com/phyloviz/phyloDB.git phyloDB.git

### Building

    cd phyloDB.git
    ./configure.sh # build phyloDB's JAR files.
    cd docker
    ./build-docker.sh # fetch latest stable Neo4j APOC libraries and build the phyloDB Docker image.

At this point, if no issue has occurred, the build process will be at [step four of the Wiki guide](https://github.com/phyloviz/phyloDB/wiki/Initialization-scripts-in-the-database-container).
The [Wiki](https://github.com/phyloviz/phyloDB/wiki) of this project documents several topics, namely archictecural views, deployment, authentication and API definition (usage). As previously mentioned, the deployment steps present in the wiki can be skiped to step 4 if you have done the previous steps that automatize steps 1 to 3.

### Easy launch

After performing the build steps, the phylodb Docker containers may be launched (you should be in directory `phyloDB.git/docker`):

    ./start-docker.sh

This will launch the following containers:

- **docker-app-1**: contains the phyloDB server logic.
- **docker-db-1**: contains the Neo4j database where project data is stored.

The Docker container internal files will be mapped to the directory `phyloDB.git/docker/instance` in your local machine:

- `phyloDB.git/docker/instance/app`: files corresponding to the phyloDB server logic.
- `phyloDB.git/docker/instance/db`: files corresponding to the Neo4j database data.

The container files themselves are excluded from this repository through `phyloDB.git/docker/.gitignore`.

### Stopping the containers

Again, you should be in directory `phyloDB.git/docker`.

    ./stop-docker.sh

### Testing

A prestine Neo4j instance should be running for testing.

Unit tests:

    cd phylodb
    ./gradlew test --tests pt.ist.meic.phylodb.unit*

Performance tests:

    cd phylodb
    ./gradlew test --tests pt.ist.meic.phylodb.performance.AlgorithmBenchmarks
    ./gradlew test --tests pt.ist.meic.phylodb.performance.OperationBenchmarks

Algorithm module tests (a Neo4j instance is not required):

    cd algorithms
    mvn test


# More documentation
The [Wiki](https://github.com/phyloviz/phyloDB/wiki) provides an overview of detailed installation steps, the purpose and usage of phyloDB.

# Architectural View
 
<img src=https://github.com/phyloviz/phyloDB/blob/master/wiki/images/client-server.png width=400 height=300>

# Team

* Bruno Lourenço (Developer)
* Miguel Coimbra (Developer, Maintainer)
* Cátia Vaz (Supervisor, Maintainer) (cvaz at cc.isel.ipl.pt)
* Alexandre Francisco (Supervisor Maintainer) (aplf at ist.utl.pt)
