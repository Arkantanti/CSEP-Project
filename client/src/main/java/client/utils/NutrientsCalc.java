package client.utils;

import commons.Ingredient;
import commons.RecipeIngredient;
import commons.Unit;

import java.util.List;

public class NutrientsCalc {


    /**
     * Logic for calculating the amount of calories for this Recipe.
     * This logic assumes that 1g = 1mL.
     * @param ingredients ingredient list with calculateCalories method returning in unit kcal/g
     * @return amount of calories or 0.0 in case of invalid ingredient's mass (kcal/100g)
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

    /**
     * Calculates the total amounts of nutrients for a list of ingredients.
     * @param ingredients List of RecipeIngredients.
     * @return 3 element array with double sums.
     */
    public double[] calculateNutrients(List<RecipeIngredient> ingredients) {
        double totalCarbs = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalMass = 0;
        for(RecipeIngredient ri: ingredients) {
            if(ri == null) continue;
            if(ri.getIngredient() == null || ri.getUnit() == Unit.CUSTOM) continue;
            double amount = ri.getAmount();
            Ingredient ingredient = ri.getIngredient();
            double mass = ri.getUnit() == Unit.GRAM ? amount : amount*1000;
            totalCarbs += ingredient.getCarbs() * mass;
            totalProtein += ingredient.getProtein() * mass;
            totalFat += ingredient.getFat() * mass;
            totalMass += mass;
        }
        if (totalMass <= 0.0) return new double[]{0.0, 0.0, 0.0};
        totalCarbs = totalCarbs/totalMass;
        totalProtein = totalProtein/totalMass;
        totalFat = totalFat/totalMass;
        return new double[]{totalCarbs, totalProtein, totalFat};
    }
}
