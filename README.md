# CSEP Template Project

This repository contains the template for the CSE project. Please extend this README.md with sufficient instructions that will illustrate for your TA and the course staff how they can run your project.

To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

	mvn -pl server -am spring-boot:run

to run the server and

	mvn -pl client -am javafx:run

to run the client. Please note that the server needs to be running, before you can start the client.

Get the template project running from the command line first to ensure you have the required tools on your sytem.

Once it is working, you can try importing the project into your favorite IDE. Especially the client is a bit more tricky to set up there due to the dependency on a JavaFX SDK.
To help you get started, you can find additional instructions in the corresponding README of the client project.

## Database Startup Configuration

The server has different modes for loading data when it starts, you can choose how much demo data you want to have loaded into the database.

### How to choose a database startup variant

There are 3 variants you can use:
- 'large' - this loads alot of data (20+ recipes with ingredients) this is the default one
- 'small' - this loads just a bit of data (3 recipes only)
- 'empty' - this loads nothing, so the database is completely empty

To change the mode you have two options.

**Option 1 - Edit the properties file**

Go to 'server/src/main/resources/application.properties' and change this line:

db.mode=large

You can change 'large' to 'small' or 'empty', then just run the server normally.

**Option 2 - Use command line arguments**

You can also pass the mode trough command line arguments when starting the server:

mvn -pl server -am spring-boot:run '-Dspring-boot.run.arguments=--db.mode=small'

or for empty:

mvn -pl server -am spring-boot:run '-Dspring-boot.run.arguments=--db.mode=empty'

### Making the database persistent

By default the database deletes everything when you stop the server. If you want to keep the data peristent between runs, you need to edit 'application.properties':

Uncomment these lines (remove the #):

#spring.datasource.url=jdbc:h2:file:./h2-database
#spring.jpa.hibernate.ddl-auto=update

And comment these lines (add #):
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create