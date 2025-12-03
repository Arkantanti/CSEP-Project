/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server;

import java.util.List;
import java.util.Random;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.database.IngredientRepository;
import server.database.RecipeIngredientRepository;
import server.database.RecipeRepository;

@Configuration
public class Config {

    @Bean
    public Random getRandom() {
        return new Random();
    }

    /**
     * Seeds the database with test data on startup.
     */
    @Bean
    public CommandLineRunner demoData(RecipeRepository recipeRepo,
                                      IngredientRepository ingredientRepo,
                                      RecipeIngredientRepository recipeIngredientRepo) {
        return args -> {
            // 1. Create Ingredients
            Ingredient flour = ingredientRepo.save(new Ingredient("Flour", 1.0, 10.0, 76.0));
            Ingredient sugar = ingredientRepo.save(new Ingredient("Sugar", 0.0, 0.0, 100.0));
            Ingredient egg = ingredientRepo.save(new Ingredient("Egg", 11.0, 13.0, 1.1));
            Ingredient tomato = ingredientRepo.save(new Ingredient("Tomato", 0.2, 0.9, 3.9));
            Ingredient bread = ingredientRepo.save(new Ingredient("Bread", 3.2, 9.0, 49.0));
            Ingredient cheese = ingredientRepo.save(new Ingredient("Cheese", 25.0, 25.0, 1.3));

            // 2. Create Recipes
            Recipe pancake = recipeRepo.save(new Recipe("Pancakes", 4,
                    List.of("Mix ingredients", "Fry in pan", "Serve hot")));
            Recipe tomatoSoup = recipeRepo.save(new Recipe("Tomato Soup", 2,
                    List.of("Boil Water", "Add Tomato")));
            Recipe grilledCheese = recipeRepo.save(new Recipe("Grilled Cheese", 1,
                    List.of("Toast Bread", "Melt Cheese")));

            // 3. Create RecipeIngredients for Pancakes
            recipeIngredientRepo.save(new RecipeIngredient(pancake,
                    flour, null, 200.0, Unit.GRAM));
            recipeIngredientRepo.save(new RecipeIngredient(pancake,
                    sugar, null, 50.0, Unit.GRAM));
            recipeIngredientRepo.save(new RecipeIngredient(pancake,
                    egg, null, 2.0, Unit.CUSTOM));

            // 4. Create RecipeIngredients for Tomato Soup
            recipeIngredientRepo.save(new RecipeIngredient(tomatoSoup,
                    tomato, null, 500.0, Unit.GRAM));

            // 5. Create RecipeIngredients for Grilled Cheese
            recipeIngredientRepo.save(new RecipeIngredient(grilledCheese,
                    bread, null, 2.0, Unit.CUSTOM));
            recipeIngredientRepo.save(new RecipeIngredient(grilledCheese,
                    cheese, null, 100.0, Unit.GRAM));
        };
    }
}