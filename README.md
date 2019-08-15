SOME_COMPANY Test Suite
==

Project which is supposed to create an easy Web Interface to manage Files for Story driven testing for a German Insurance Company.

Consists out of an AngularJS 1.5.5 Frontend in a Node Container, a Python3.6 API in an Alpine Container and a Java Backend as well as a MySQL Container.

Goal of the project was to develop an MVP for the Company to evaluate the Usage of a Test Scenario Management System for their Story Driven Testing. At the End of the Project there was a live Presentation and Demo for the Head of Development of the Insurance Company as well as some of his peers.

Application Text is in German. Was created as a Team of 2, other guy wrote the Java Backend.

## Functionalities

One is able to:
* Import Test Stories and Name them
* Import Test Data and link it to a Test Story
* Create Entities which can then be linked to Features of Test Data to efficiently create and manage Test Objects
* Export all Data belonging to a certain Story
* Recombine and Export created Entities
* Choose to create all possible Permutations or keep order on export
* See some stats about Entities and Entity Counts
* Filter on Export, based on Feature Types, available Filters are: Regex, Min/Max for Numbers

## Requirements

Other: 
1. Docker
2. Maven

## Start the project/system (Front End Client + API + Back End) Client + Back End: 

Currently the project is configured to run on one machine with every component running inside its own Docker Container. 

1. Clone or download the repository with both frond end and back end projects
2. Navigate to the database micro-service directory and write in a terminal/command prompt: "mvn clean package -DskipTests docker:build"
3. Navigate to the importer micro-service directory and write in a terminal/command prompt: "mvn clean package -DskipTests docker:build"
4. Navigate to the exporter micro-service directory and write in a terminal/command prompt: "mvn clean package -DskipTests docker:build"
5. Navigate to the root directory of the project and write in a terminal/command prompt: "docker-compose up --build --force-recreate"
6. The Front End Client should be accessible at http://localhost

### Ports for each micro service, including Front End and MySQL Database
Front End Port: 80 
API Port: 40042
Database Port: 8081
Exporter Port: 8082
Importer Port: 8083
MySQL Port: 3306
