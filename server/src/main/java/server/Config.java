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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.database.IngredientRepository;
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
    public CommandLineRunner demoData(RecipeRepository recipeRepo, IngredientRepository ingredientRepo) {
        return args -> {
            // 1. Create Ingredients
            Ingredient flour = ingredientRepo.save(new Ingredient("Flour", 1.0, 10.0, 76.0));
            Ingredient sugar = ingredientRepo.save(new Ingredient("Sugar", 0.0, 0.0, 100.0));
            Ingredient egg = ingredientRepo.save(new Ingredient("Egg", 11.0, 13.0, 1.1));

            // 2. Create Recipe
            Recipe pancake = new Recipe("Pancakes", 4, List.of("Mix ingredients", "Fry in pan", "Serve hot"));

            // For a simple test, just saving the Recipe name is enough to prove the list works:
            recipeRepo.save(pancake);
            recipeRepo.save(new Recipe("Tomato Soup", 2, List.of("Boil Water", "Add Tomato")));
            recipeRepo.save(new Recipe("Grilled Cheese", 1, List.of("Toast Bread", "Melt Cheese")));
        };
    }
}