/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;


import java.util.List;

import client.config.Config;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

/**
 * Utility class for communicating with the server via REST.
 */
public class ServerUtils {

    private final String serverURL;
    private final Client client;

    /**
     * Constructor for the ServerUtils class.
     * @param cfg Client config object. Injected with Guice.
     */
    @Inject
    public ServerUtils(Config cfg) {
        // EXISTING Constructor
        // Guice uses this one.
        serverURL = cfg.getServerUrl();
        this.client = ClientBuilder.newClient(new ClientConfig());
    }

    /**
     * NEW Constructor (For Testing Only)
     * We remove @Inject so Guice ignores it. We use this in your Test class.
     * @param cfg Client config object. Injected with Guice.
     * @param client the fictional client we use for testing
     */
    public ServerUtils(Config cfg, Client client) {
        this.serverURL = cfg.getServerUrl();
        this.client = client;
    }

    /**
     * Retrieves a list of all recipes from the server.
     *
     * @return a list of Recipe objects
     */
    public List<Recipe> getRecipes() {
        return this.client
                .target(serverURL).path("api/recipes/")
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Recipe>>() {
                });
    }

    /**
     * Searches for recipes matching the query.
     *
     * @param query the search string (name, ingredient, or step)
     * @return a list of matching recipes
     */
    public List<Recipe> searchRecipes(String query) {
        return this.client
                .target(serverURL).path("api/recipes/search")
                .queryParam("name", query) // Matches @RequestParam("name") in your Controller
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Recipe>>() {});
    }

    /**
     * Retrieves all recipe ingredients for a specific recipe from the server.
     *
     * @param recipeId the ID of the recipe
     * @return a list of RecipeIngredient objects
     */
    public List<RecipeIngredient> getRecipeIngredients(long recipeId) {
        return this.client
                .target(serverURL).path("api/recipeingredients/by-recipe/" + recipeId)
                .request(APPLICATION_JSON)
                .get(new GenericType<List<RecipeIngredient>>() {
                });
    }

    /**
     * Checks if the server is currently reachable.
     *
     * @return true if the server is reachable, false otherwise
     */
    public boolean isServerAvailable() {
        try {
            this.client
                    .target(serverURL)
                    .request(APPLICATION_JSON)
                    .get();
        } catch (ProcessingException e) {
            return false;
        }
        return true;
    }

    /**
     * Updates the specified recipe
     * @param recipe the recipe to update
     * @return the updated recipe as returned by the server
     * @throws IllegalArgumentException if {@code recipe} is null or has invalid ID
     */
    public Recipe updateRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe to update must not be null");
        }
        if (recipe.getId() < 0) {
            throw new IllegalArgumentException("Recipe to update must have a valid ID");
        }

        try {
            return this.client
                    .target(serverURL)
                    .path("api/recipes/" + recipe.getId())
                    .request(APPLICATION_JSON)
                    .put(Entity.entity(recipe, APPLICATION_JSON), Recipe.class);
        } catch (ProcessingException e) {
            return null;
        }
    }

    /**
     * this is to create a new recipe
     *
     * @param recipe recipe of the recipe.
     * @return a new recipe
     */
    public Recipe add(Recipe recipe){
        return this.client
                .target(serverURL).path("/api/recipes/")
                .request(APPLICATION_JSON)
                .post(Entity.entity(recipe, APPLICATION_JSON), Recipe.class);
    }


    /**
     * gets all ingredients in the database
     * @return a list of ingredients in the database
     */
    public List<Ingredient> getIngredients(){
        return this.client
                .target(serverURL).path("api/ingredients/")
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Ingredient>>() {
                });
    }

    /**
     * This function is for deleting recipes
     * @param id The id of the recipe that needs to be deleted
     * @return The deletion of the recipe in the client
     */
    public Response deleteRecipe(long id){
        if (id < 0) {
            throw new IllegalArgumentException("Recipe Ingredient to update must have a valid ID");
        }

        try {
            this.client
                    .target(serverURL)
                    .path("/api/recipes/" + id)
                    .request()
                    .delete();
        } catch (ProcessingException e) {
            System.out.println("something went wrong;");
        }
        return null;
    }

    /**
     * Updates the specified recipe ingredient
     * @param recipeIngredient the recipe ingredient to update
     * @return the updated recipe ingredient as returned by the server
     * @throws IllegalArgumentException if {@code recipe ingredient} is null or has invalid ID
     */
    public RecipeIngredient updateRecipeIngredient(RecipeIngredient recipeIngredient) {
        if (recipeIngredient == null) {
            throw new IllegalArgumentException("Recipe Ingredient to update must not be null");
        }
        if (recipeIngredient.getId() < 0) {
            throw new IllegalArgumentException("Recipe Ingredient to update must have a valid ID");
        }

        try {
            return this.client
                    .target(serverURL)
                    .path("api/recipeingredients/" + recipeIngredient.getId())
                    .request(APPLICATION_JSON)
                    .put(Entity.entity(recipeIngredient, APPLICATION_JSON), RecipeIngredient.class);
        } catch (ProcessingException e) {
            return null;
        }
    }

    /**
     * Deletes the specified recipe ingredient
     * @param id the id of the recipe ingredient to delete
     * @throws IllegalArgumentException if id is invalid
     */
    public Response deleteRecipeIngredient(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Recipe Ingredient to update must have a valid ID");
        }

        try {
            Response r = this.client
                    .target(serverURL)
                    .path("api/recipeingredients/" + id)
                    .request(APPLICATION_JSON)
                    .delete();
            return r;
        } catch (ProcessingException e) {
            return null;
        }
    }

    /**
     * Adds a RecipeIngredient to the server database and returns one with a valid ID
     */
    public RecipeIngredient addRecipeIngredient(RecipeIngredient recipeIngredient) {
        if (recipeIngredient == null) {
            throw new IllegalArgumentException("Recipe Ingredient to add must have a valid ID");
        }
        if (recipeIngredient.getId() < 0) {
            throw new IllegalArgumentException("Recipe Ingredient to add must have a valid ID, currently "
                    + recipeIngredient.getId());
        }
        try {
            return this.client
                    .target(serverURL)
                    .path("api/recipeingredients/")
                    .request(APPLICATION_JSON)
                    .post(Entity.entity(recipeIngredient, APPLICATION_JSON), RecipeIngredient.class);
        }
        catch (ProcessingException e) {
            return null;
        }
    }

}