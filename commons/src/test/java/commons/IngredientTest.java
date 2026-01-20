package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {
    Ingredient ingredient;
    Ingredient ingredient2;
    Ingredient ingredient3;

    @BeforeEach
    void setUp(){
        ingredient = new Ingredient("fat", 50, 24, 45);
        ingredient2 = new Ingredient("fly", 50, 24, 45);
        ingredient3 = new Ingredient("fat", 50, 24, 45);
    }

    @Test
    void getName() {
        assertEquals("fat", ingredient.getName());
    }

    @Test
    void getFat() {
        assertEquals(50, ingredient.getFat());
    }

    @Test
    void getProtein() {
        assertEquals(24, ingredient.getProtein());
    }

    @Test
    void getCarbs() {
        assertEquals(45, ingredient.getCarbs());

    }

    @Test
    void setName() {
        ingredient.setName("Hello");
        assertEquals("Hello", ingredient.getName());
    }

    @Test
    void setFat() {
        ingredient.setFat(40);
        assertEquals(40, ingredient.getFat());
    }

    @Test
    void setCarbs() {
        ingredient.setCarbs(40);
        assertEquals(40, ingredient.getCarbs());
    }

    @Test
    void setProtein() {
        ingredient.setProtein(40);
        assertEquals(40, ingredient.getProtein());
    }

    @Test
    void equalsTest(){
        assertEquals(ingredient3, ingredient);
    }

    @Test
    void equalsFalseTest(){
        assertNotEquals(ingredient2, ingredient);
    }

    @Test
    void equalsNullTest(){
        assertNotEquals(null, ingredient);
    }
    @Test
    void getId() {
        assertEquals(0, ingredient.getId());
    }

    @Test
    void setId() {
        ingredient.setId(10);
        assertEquals(10, ingredient.getId());
    }

    @Test
    void testHashCode() {
        assertEquals(ingredient.hashCode(), ingredient3.hashCode());
        assertNotEquals(ingredient.hashCode(), ingredient2.hashCode());
    }

    @Test
    void testToString() {
        String result = ingredient.toString();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("fat"));
    }

    @Test
    void calculateCalories_returnsKcalPerGram() {
        double result = ingredient.calculateCalories();
        assertEquals(7.26, result, 1e-9);
    }
}