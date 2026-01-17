 # CSEP Project - FoodPal

## Table of Contents
- [Running the app](#running-the-app)
- [Database startup configuration](#database-startup-configuration)
- [App description](#app-description)
- [Using the app](#using-the-app)

 ## Running the app
This repository contains the template for the CSE project. Please extend this README.md with sufficient instructions that will illustrate for your TA and the course staff how they can run your project.

To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

    mvn -pl server -am spring-boot:run

to run the server and

	mvn -pl client -am javafx:run

to run the client. Please note that the server needs to be running, before you can start the client.
### Options
- The server startup command can be run together with database configuration argument - read more [here](#server-db-args).
- The client startup command can be run together with a `--cfg` argument and a path to specify the location of a `client-config.json` file to store client specific data.

## Database startup configuration

The server has different modes for loading data when it starts, you can choose how much demo data you want to have loaded into the database and whether you want it to persist.

### Choosing a database startup variant

There are 3 variants you can use:
- `large` - this loads alot of data (20+ recipes with ingredients) this is the default one
- `small` - this loads just a bit of data (3 recipes only)
- `empty` - this loads nothing, so the database is completely empty

To change the mode you have two options.

**Option 1 - Edit the properties file**

Go to `server/src/main/resources/application.properties` and change this line:

    db.mode=large

You can change `large` to `small` or `empty`, then just run the server normally.

**Option 2 - Use command line arguments**
<a id="server-db-args"></a>
You can also pass the mode trough command line arguments when starting the server:

    mvn -pl server -am spring-boot:run '-Dspring-boot.run.arguments=--db.mode=small'

or for empty:

    mvn -pl server -am spring-boot:run '-Dspring-boot.run.arguments=--db.mode=empty'

### Making the database temporary

By default, the database saves everything when you stop the server. If you want to reset the database on every rerun, you need to edit `application.properties`:

Comment these lines (add #):

    spring.datasource.url=jdbc:h2:file:./h2-database
    spring.jpa.hibernate.ddl-auto=update

And uncomment these lines (remove #):

    #spring.datasource.url=jdbc:h2:mem:testdb    
    #spring.jpa.hibernate.ddl-auto=create

## App description
FoodPal is a cooking organizer built in a client/server setup. Users run a JavaFX client that connects to a Spring-based server to create, browse, and manage a shared collection of recipes and ingredients. 

### Basic Features

- Server-based recipe library: store and name recipes on a shared server so everyone can browse and open full details.

- Full editing: create/update/delete recipes, including their ingredients and preparation steps, with changes saved to the server.

 - Other actions: clone a recipe, refresh manually if needed, and export a printable version.

_(Basic Requirements fully implemented)_

### Advanced Features
##### Nutritional Value
- Ingredient nutrition database: manage a global ingredient list with fat/protein/carbs per 100g and an auto kcal/100g estimate, searchable/sorted by name.

- Recipe nutrition & scaling: normalized units (e.g., 1000g → 1kg), estimated kcal/100g for recipes and scale nutrition/servings/amounts by arbitrary factor.

- Other actions: pick ingredients from a dropdown, rename propagates into recipes, and show usage counts + warnings when deleting (optionally remove it from all recipes).

- Extra functionality: mark ingredients with allergens warnings from an extensive list, see the warnings summary through recipes 

_(Feature implemented fully)_
##### Searching for recipes

_(Feature implemented fully)_

##### Shopping list

_(Feature implemented fully)_

##### Live language switch

_(Feature implemented fully)_

## Using the app

