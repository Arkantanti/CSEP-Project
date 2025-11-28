package client.utils;

import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RecipePrinter {
    /**
     * Transforms a Recipe object into a String with markdown notation and saves it as .md file
     * that can later be printed.
     *
     * @param path Where to save the file
     * @param recipe {@link Recipe} object which is to be saved in markdown
     * @throws IOException If the saving fails
     */
    public static void recipePrint(Path path, Recipe recipe) throws IOException {
        StringBuilder output = new StringBuilder();
        output.append("## ").append(recipe.getName()).append("\n---");
        output.append("\n**Servings:** ").append(recipe.getServings());
        output.append("\n\n**Ingredients:**");
        for(RecipeIngredient ing : recipe.getIngredients()) {
            output.append("\n - ").append(ing.getIngredient().getName());
            output.append(" - ").append(ing.getAmount()!=0 ? ing.getAmount() : "");
            output.append(ing.getUnit()==Unit.CUSTOM ? ing.getInformalUnit() : ing.getUnit());
        }
        output.append("\n\n**Preparation steps:**");
        for(int i=1; i<=recipe.getPreparationSteps().size(); i++) {
            output.append("\n").append(i).append(". ");
            output.append(recipe.getPreparationSteps().get(i-1));             // Add auto line-breaks
        }
        output.append("\nHAVE A GOOD MEAL!!");

        Files.writeString(path, output.toString());
    }
}
