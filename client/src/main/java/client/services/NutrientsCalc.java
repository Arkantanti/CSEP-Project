package client.services;

import commons.Ingredient;
import commons.RecipeIngredient;
import commons.Unit;

import java.util.List;

public class NutrientsCalc {


    /**
     * Logic for calculating the amount of calories for this Recipe.
     * This logic assumes that 1g = 1mL.
     * @return amount of calories or 0.0 in case of invalid ingredient's mass
     */
    public double calculateCaloriesForRecipe(List<RecipeIngredient> ingredients) {
        double totalCalories = 0;
        double totalMass = 0;
        for(RecipeIngredient ri: ingredients){
            if(ri == null) continue;
            if(ri.getIngredient() == null) continue;

            //String informal = ri.getInformalUnit();
            if (ri.getUnit() == Unit.CUSTOM) continue;

            double amount = ri.getAmount();
            Ingredient ingredient = ri.getIngredient();
            totalCalories +=
                    ri.getUnit() == Unit.GRAM ?
                            ingredient.calculateCalories()*amount :
                            ingredient.calculateCalories()*amount*1000;
            totalMass += ri.getUnit() == Unit.GRAM ? amount : amount*1000;
        }
        if(totalMass <= 0.0) return 0.0;
        return 100*totalCalories/totalMass;
    }
}
