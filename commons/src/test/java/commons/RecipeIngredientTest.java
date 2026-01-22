package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static commons.Unit.LITER;
import static org.junit.jupiter.api.Assertions.*;

class RecipeIngredientTest {
    RecipeIngredient recipeIngredient;
    RecipeIngredient recipeIngredient2;
    Recipe recipe;
    Recipe recipe2;
    Ingredient sugar;
    Ingredient flour;

    @BeforeEach
    void setUp(){
        ArrayList<String> preparationList = new ArrayList<>(Arrays.asList("Food", "place"));
        sugar = new Ingredient("Sugar", 0.0, 0.0, 56.0, Set.of());
        flour = new Ingredient("Sugar", 0.0, 10.0, 78.0, Set.of());
        recipe = new Recipe("Cheese", 3, preparationList,false,false,false);
        recipe2 = new Recipe("CheeseBread", 3, preparationList,false,false,false);
        recipeIngredient = new RecipeIngredient(recipe, sugar, "", 12.0, Unit.GRAM);
        recipeIngredient2 = new RecipeIngredient(recipe2, flour, "", 12.0, Unit.GRAM);
    }

    @Test
    void getId() {
        assertEquals(0, recipeIngredient.getId());
    }

    @Test
    void getRecipe() {
        assertEquals(recipe, recipeIngredient.getRecipe());;
    }

    @Test
    void getIngredient() {
        assertEquals(sugar, recipeIngredient.getIngredient());
    }

    @Test
    void getInformalUnit() {
        assertEquals("", recipeIngredient.getInformalUnit());
    }

    @Test
    void getAmount() {
        assertEquals(12.0, recipeIngredient.getAmount());
    }

    @Test
    void getUnit() {
        assertEquals(Unit.GRAM, recipeIngredient.getUnit());
    }

    @Test
    void setId() {
        recipeIngredient.setId(5);
        assertEquals(5, recipeIngredient.getId());
    }

    @Test
    void setRecipe() {
        recipeIngredient.setRecipe(recipe2);
        assertEquals(recipe2, recipeIngredient.getRecipe());
    }

    @Test
    void setIngredient() {
        recipeIngredient.setIngredient(flour);
        assertEquals(flour, recipeIngredient.getIngredient());
    }

    @Test
    void setInformalUnit() {
        recipeIngredient.setInformalUnit("A pinch");
        assertEquals("A pinch", recipeIngredient.getInformalUnit());
    }

    @Test
    void setAmount() {
        recipeIngredient.setAmount(20.5);
        assertEquals(20.5, recipeIngredient.getAmount());
    }

    @Test
    void setUnit() {
        recipeIngredient.setUnit(Unit.GRAM);
        assertEquals(Unit.GRAM, recipeIngredient.getUnit());
    }

    @Test
    void formatIngredient() {
        String result = recipeIngredient.formatIngredient();

        assertNotNull(result);
        assertTrue(result.contains("12"));
        assertTrue(result.contains("Sugar"));
    }

    @Test
    void formatIngredientScaled() {
        double scaledAmount = 24.0;

        String result = recipeIngredient.formatIngredientScaled(scaledAmount);

        assertNotNull(result);
        assertTrue(result.contains("24"));
        assertTrue(result.contains("Sugar"));
    }

    @Test
    void formatIngredientInternal() {
        String result = recipeIngredient.formatIngredientInternal(12.0);

        assertNotNull(result);
        assertTrue(result.contains("12"));
        assertTrue(result.contains("Sugar"));
    }

    @Test
    void testEquals() {
        RecipeIngredient same =
                new RecipeIngredient(recipe, sugar, "", 12.0, Unit.GRAM);

        RecipeIngredient different =
                new RecipeIngredient(recipe2, sugar, "", 12.0, Unit.GRAM);

        assertEquals(recipeIngredient, same);
        assertNotEquals(recipeIngredient, different);
        assertNotEquals(null, recipeIngredient);
    }

    @Test
    void testHashCode() {
        RecipeIngredient same =
                new RecipeIngredient(recipe, sugar, "", 12.0, Unit.GRAM);

        assertEquals(recipeIngredient.hashCode(), same.hashCode());
    }

    @Test
    void testToString() {
        String result = recipeIngredient.toString();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("Sugar"));
    }
}