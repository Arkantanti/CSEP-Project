package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeTest {

    Recipe recipe;
    Recipe recipe2;
    Recipe recipeSame;

    @BeforeEach
    void setUp() {
        List<String> steps = new ArrayList<>(Arrays.asList("Step 1", "Step 2"));
        recipe = new Recipe("Cake", 4, steps, "English",false,false,false);
        recipe2 = new Recipe("Bread", 2, steps, "English", true, true, true);
        recipeSame = new Recipe("Cake", 4, new ArrayList<>(steps), "English",false,false,false);
    }

    @Test
    void getId() {
        assertEquals(0, recipe.getId());
    }

    @Test
    void getName() {
        assertEquals("Cake", recipe.getName());
    }

    @Test
    void getServings() {
        assertEquals(4, recipe.getServings());
    }

    @Test
    void getPreparationSteps() {
        assertEquals(2, recipe.getPreparationSteps().size());
        assertEquals("Step 1", recipe.getPreparationSteps().get(0));
    }

    @Test
    void setName() {
        recipe.setName("Pie");
        assertEquals("Pie", recipe.getName());
    }

    @Test
    void setId() {
        recipe.setId(10L);
        assertEquals(10L, recipe.getId());
    }

    @Test
    void setServings() {
        recipe.setServings(6);
        assertEquals(6, recipe.getServings());
    }

    @Test
    void setPreparationSteps() {
        ArrayList<String> newSteps = new ArrayList<>(Arrays.asList("New step", "Second step"));
        recipe.setPreparationSteps(newSteps);
        assertEquals(newSteps, recipe.getPreparationSteps());
    }

    @Test
    void addPreparationStep() {
        recipe.addPreparationStep("Final step");
        assertEquals(3, recipe.getPreparationSteps().size());
        assertTrue(recipe.getPreparationSteps().contains("Final step"));
    }

    @Test
    void testEquals() {
        assertEquals(recipe, recipeSame);
        assertNotEquals(recipe, recipe2);
        assertNotEquals(recipe, null);
    }

    @Test
    void testHashCode() {
        assertEquals(recipe.hashCode(), recipeSame.hashCode());
    }

    @Test
    void testToString() {
        String result = recipe.toString();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("Cake"));
    }
    @Test
    void testBooleans() {
        assertFalse(recipe.isCheap());
        assertFalse(recipe.isFast());
        assertFalse(recipe.isVegan());

        assertTrue(recipe2.isCheap());
        assertTrue(recipe2.isFast());
        assertTrue(recipe2.isVegan());
    }

    @Test
    void setBooleans() {
        recipe.setCheap(true);
        recipe.setFast(true);
        recipe.setVegan(true);

        assertTrue(recipe.isCheap());
        assertTrue(recipe.isFast());
        assertTrue(recipe.isVegan());
    }
}