# CSEP Project - FoodPal

## Table of Contents
- [Running the app](#running-the-app)
- [Database startup configuration](#database-startup-configuration)
- [App description](#app-description)
- [Using the app](#using-the-app)

## Running the app
This repository contains the template for the CSE project. Please extend this README.md with sufficient instructions that will show your TA and the course staff how they can run your project.

To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute:

    mvn -pl server -am spring-boot:run

to run the server, and

    mvn -pl client -am javafx:run

to run the client. Please note that the server needs to be running before you can start the client.

### Options
- The server startup command can be run together with a database configuration argument — read more [here](#server-db-args).
- The client startup command can be run together with a `--cfg` argument and a path to specify the location of a `client-config.json` file to store client-specific data.

## Database startup configuration

The server has different modes for loading data when it starts. You can choose how much demo data you want to have loaded into the database and whether you want it to persist.

### Choosing a database startup variant

There are 3 variants you can use:
- `large` - this loads a lot of data (20+ recipes with ingredients). This is the default one.
- `small` - this loads just a bit of data (3 recipes only).
- `empty` - this loads nothing, so the database is completely empty.

To change the mode you have two options.

**Option 1 - Edit the properties file**

Go to `server/src/main/resources/application.properties` and change this line:

    db.mode=large

You can change `large` to `small` or `empty`, then just run the server normally.

**Option 2 - Use command line arguments**
<a id="server-db-args"></a>
You can also pass the mode through command line arguments when starting the server:

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

#### Automated Change Synchronization
- TODO

_(Feature implemented partially)_

#### Nutritional Value
- Ingredient nutrition database: manage a global ingredient list with fat/protein/carbs per 100g and an auto kcal/100g estimate, searchable/sorted by name.
- Recipe nutrition & scaling: normalize units (e.g., 1000g → 1kg), estimate kcal/100g for recipes, and scale nutrition/servings/amounts by an arbitrary factor.
- Other actions: pick ingredients from a dropdown, renaming propagates into recipes, and show usage counts + warnings when deleting (optionally remove it from all recipes).
- Extra functionality: TODO

_(Feature implemented fully for excellent)_

#### Searching for recipes
- Favorites management: star/unstar recipes, view favorites in a dedicated list, keep favorites local, and store them as references (not copies) so changes propagate and renames don’t break them. Warn the user if a favorite was deleted by someone else.
- Search experience: full-text search across recipes (name/ingredients/instructions), support multi-term queries (AND), filter the current recipe view (including favorites + all recipes), and allow quick cancel via Esc.
- Extra functionality: tag recipes as vegan/fast/cheap, use those keywords in multi-term queries (e.g., "cheap pizza").

_(Feature implemented fully for excellent)_

#### Shopping list
- Local, simple shopping list: create a flat, ordered shopping list (not stored on the server) for planning grocery trips, with a reset option.
- Flexible adding & editing: add/remove items directly, add ingredients from recipes, and review an editable overview before confirming—adjust amounts and include arbitrary extra items.
- Duplicates + export: ingredients in the shopping list display which recipe they were added from, and allow downloading a printable version.

_(Feature implemented fully for excellent)_

#### Live language switch
- TODO

_(Feature implemented fully)_

## Using the app

### View Modes
View modes control which content is displayed on the main content list. The buttons above the content list control which view mode is active. The available view modes are:
- Favorite recipes ("★" button)
- All recipes ("Recipes" button)
- All ingredients ("Ingredients" button)

Furthermore, the current view mode also changes the functionality of other controls:
- Search bar - hidden when in "all ingredients", otherwise displayed.
- "+" button below the content list - adds a new ingredient in "all ingredients", otherwise adds a new recipe.
- Refresh button in top right - refreshes the content list with fresh changes from the server.

### Basic usage
- Ingredient and recipe editing can be accessed through individual entries after opening them through the content list.
- Ingredients can also be added "on the go" when editing or adding a recipe's ingredient through the purple "+" button.
- Adding can be done through the "+" button below the content list.
- Deleting can be done through individual entries after opening them through the content list - "🗑" button.

When creating a new ingredient, editing an existing one, or editing an existing recipe, the following constraints apply:
- Name must be unique among all ingredients/recipes, respectively.
- Name must not be blank.

When creating a new recipe, the following constraints apply:
- Name must be unique among all recipes.
- Name must not be blank.
- At least one ingredient has to be added.
- At least one preparation step has to be added.
- Servings must be an integer number greater than 0.

When inspecting a recipe, the following two options are available:
- To clone a recipe and edit it before it is added to the database - "⎅" button next to the recipe name.
- To export a recipe into PDF format - "🖶" button next to the recipe name.

### Recipe Scaling
The target servings field of a recipe can be used to scale all amounts that are part of the recipe to the user's liking. To do this, input an arbitrary positive factor into the field. The app will then calculate the multiplying factor based on the original servings and change all numeric amounts of ingredients on this page. To come back to the original servings number, the reset button on the right of the target servings field can be used.

### Searching through recipes
The search bar above the content list can be used to filter through all recipes or through favorite recipes (depending on the view mode). The search bar supports multi-term searching among preparation step strings, ingredient names, and recipe names. Spaces inside the query are treated as ANDs. Esc can be used to cancel the search.

### Favorites
Recipes can be marked as a favorite with the "★" button next to the recipe name. Favorites are stored between server/app runs and are stored locally in the client config.

### Shopping list
The "shopping list" button in the top left opens the shopping list window. In the window, all items of the shopping list are displayed. There are two types of items:
- Any text entry - can use any text string.
- Any ingredient entry - has to use a valid database ingredient.

Furthermore, the shopping list window includes two control buttons:
- "clear" - empties the shopping list.
- "print" - exports the shopping list to PDF format.

The user can also add items to the shopping list through the recipe view. The "shop" button placed next to the target servings field adds all current ingredients of the recipe to the shopping list, taking into account the scaling factor. Before finalizing the addition, the user has an option to make extra edits in an overview.

### Language switch
TODO
