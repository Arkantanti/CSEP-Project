package client.utils;

import commons.Ingredient;
import commons.RecipeIngredient;
import commons.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NutrientsCalcTest {

    private NutrientsCalc sut;

    RecipeIngredient gramOk;
    RecipeIngredient otherUnitOk;
    RecipeIngredient custom;
    RecipeIngredient nullIngredient;

    Ingredient flour;
    Ingredient oil;

    @BeforeEach
    void setUp() {
        sut = new NutrientsCalc();

        flour = new Ingredient("flour", 1.0, 10.0, 76.0, Set.of());
        oil  = new Ingredient("oil", 100.0, 0.0, 0.0, Set.of());

        gramOk = new RecipeIngredient(null, flour, null ,100, Unit.GRAM);
        otherUnitOk = new RecipeIngredient(null, oil, null ,1, Unit.LITER);
        custom = new RecipeIngredient(null, flour, "132", 3, Unit.CUSTOM);
        nullIngredient = new RecipeIngredient(null, null, "",123, Unit.GRAM);


    }

    @Test
    void calculateCalories_nullRecipeIngredient() {

        List<RecipeIngredient> input = new ArrayList<>();
        input.add(null);
        double result = sut.calculateCaloriesForRecipe(input);
        assertEquals(0.0, result, 1e-9);
    }

    @Test
    void calculateCalories_customUnit() {

        List<RecipeIngredient> input = new ArrayList<>();
        input.add(custom);
        double result = sut.calculateCaloriesForRecipe(input);
        assertEquals(0, result, 1e-9);
    }

    @Test
    void calculateCalories_nulIngredient() {

        List<RecipeIngredient> input = new ArrayList<>();
        input.add(nullIngredient);
        double result = sut.calculateCaloriesForRecipe(input);
        assertEquals(0, result, 1e-9);
    }
    @Test
    void calculateCalories_otherUnit() {
        List<RecipeIngredient> input = new ArrayList<>();
        input.add(otherUnitOk);
        double result = sut.calculateCaloriesForRecipe(input);
        double expectedOutput = otherUnitOk.getAmount()*1000*otherUnitOk.getIngredient().calculateCalories();
        expectedOutput = expectedOutput / (otherUnitOk.getAmount()*1000);
        expectedOutput = expectedOutput * 100;

        assertEquals(expectedOutput, result, 1e-9);
    }

    @Test
    void calculateCalories_gUnit() {
        List<RecipeIngredient> input = new ArrayList<>();
        input.add(gramOk);
        double result = sut.calculateCaloriesForRecipe(input);
        double expectedOutput = gramOk.getAmount()*gramOk.getIngredient().calculateCalories();
        expectedOutput = expectedOutput / gramOk.getAmount();
        expectedOutput = expectedOutput * 100;

        assertEquals(expectedOutput, result, 1e-9);
    }

    @Test
    void calculateCalories_allIngredients() {
        List<RecipeIngredient> input = new ArrayList<>();
        input.add(gramOk);
        input.add(otherUnitOk);
        input.add(custom);
        input.add(nullIngredient);
        input.add(null);
        double totalmass = 0;
        double totalCalories =0;
        double result = sut.calculateCaloriesForRecipe(input);
        totalCalories += gramOk.getAmount()*gramOk.getIngredient().calculateCalories();
        totalCalories += otherUnitOk.getAmount()*1000*otherUnitOk.getIngredient().calculateCalories();
        totalmass += gramOk.getAmount();
        totalmass += otherUnitOk.getAmount()*1000;
        double expectedOutput = totalCalories/totalmass*100;
        assertEquals(expectedOutput, result, 1e-9);
    }

    @Test
    void calculateNutrients_nullRecipeIngredient_returnsZeros() {
        List<RecipeIngredient> input = new ArrayList<>();
        input.add(null);

        double[] result = sut.calculateNutrients(input);

        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, result, 1e-9);
    }

    @Test
    void calculateNutrients_customUnit_returnsZeros() {
        List<RecipeIngredient> input = new ArrayList<>();
        input.add(custom);

        double[] result = sut.calculateNutrients(input);

        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, result, 1e-9);
    }

    @Test
    void calculateNutrients_nullIngredient_returnsZeros() {
        List<RecipeIngredient> input = new ArrayList<>();
        input.add(nullIngredient);

        double[] result = sut.calculateNutrients(input);

        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, result, 1e-9);
    }

    @Test
    void calculateNutrients_gUnit_singleIngredient_returnsIngredientValues() {
        List<RecipeIngredient> input = new ArrayList<>();
        input.add(gramOk);

        double[] result = sut.calculateNutrients(input);

        double[] expected = new double[] {
                flour.getCarbs(),
                flour.getProtein(),
                flour.getFat()
        };

        assertArrayEquals(expected, result, 1e-9);
    }

    @Test
    void calculateNutrients_otherUnit_singleIngredient_returnsIngredientValues() {
        List<RecipeIngredient> input = new ArrayList<>();
        input.add(otherUnitOk);

        double[] result = sut.calculateNutrients(input);

        double[] expected = new double[] {
                oil.getCarbs(),
                oil.getProtein(),
                oil.getFat()
        };

        assertArrayEquals(expected, result, 1e-9);
    }

    @Test
    void calculateNutrients_allIngredients_weightedAverageIgnoringInvalid() {
        List<RecipeIngredient> input = new ArrayList<>();
        input.add(gramOk);         // 100 g flour
        input.add(otherUnitOk);    // 1 L oil -> 1000 g (per your rule)
        input.add(custom);         // ignored
        input.add(nullIngredient); // ignored
        input.add(null);           // ignored

        double[] result = sut.calculateNutrients(input);

        double flourMass = gramOk.getAmount();                 // 100
        double oilMass   = otherUnitOk.getAmount() * 1000.0;   // 1000
        double totalMass = flourMass + oilMass;                // 1100

        double expectedCarbs   = (flour.getCarbs()   * flourMass + oil.getCarbs()   * oilMass) / totalMass;
        double expectedProtein = (flour.getProtein() * flourMass + oil.getProtein() * oilMass) / totalMass;
        double expectedFat     = (flour.getFat()     * flourMass + oil.getFat()     * oilMass) / totalMass;

        assertArrayEquals(
                new double[]{expectedCarbs, expectedProtein, expectedFat},
                result,
                1e-9
        );
    }

    @Test
    void calculateNutrients_emptyList_returnsZeros() {
        double[] result = sut.calculateNutrients(List.of());
        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, result, 1e-9);
    }
}