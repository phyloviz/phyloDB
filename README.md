# phyloDB
This project provides a framework for large scale phylogenetic analysis in the form of a Web API, a graph oriented database ([Neo4j](https://neo4j.com/)), and a plugin for the latter. The goal of this project is to allow the representation of large phylogenetic networks and trees, as well as ancillary data, support queries on such data, and the deployment of algorithms for inferring/detecting patterns and for computing visualizations. It started being developed in the scope of a master thesis for IST (Instituto Superior Técnico). The unit tests and benchmarks developed are available in the [test folder](https://github.com/phyloviz/phyloDB/tree/master/phylodb/src/test/java/pt/ist/meic/phylodb) of the code.

The [wiki](https://github.com/phyloviz/phyloDB/wiki) of this project provides documentation of several topics, namely archictecural views, deployment, authentication and the api definition (usage). There videos supporting the documentation namely, a [deployment](https://www.youtube.com/watch?v=RWTc_ltefgU&feature=youtu.be&fbclid=IwAR28qwjGNMX_r3oAs-cK2z0Mjp1ONiDievc9Q5oRSv1ilIMmQ74NRKSB3Vg), an [usage](https://www.youtube.com/watch?v=kUmvlAmZSME&feature=youtu.be&fbclid=IwAR2S-xEZIRHidqqsdn0UbyaUr3r631tESYkFG7p-vftayF6evLX9o4yMXNg) example, and a [demo](https://youtu.be/QOK7p_zICMM).

 # Architectural View
 
<img src=https://github.com/phyloviz/phyloDB/blob/master/wiki/images/client-server.png width=400 height=300>

# Team
* Bruno Lourenço (Developer)
* Miguel Coimbra (Developer, Maintainer)
* Cátia Vaz (Supervisor, Maintainer) (cvaz at cc.isel.ipl.pt)
* Alexandre Francisco (Supervisor Maintainer) (aplf at cc.isel.ipl.pt)
