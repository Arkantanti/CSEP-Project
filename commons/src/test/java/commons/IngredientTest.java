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
        assertTrue(ingredient.equals(ingredient3));
    }

    @Test
    void equalsFalseTest(){
        assertFalse(ingredient.equals(ingredient2));
    }

    @Test
    void equalsNullTest(){
        assertFalse(ingredient.equals(null));
    }
}