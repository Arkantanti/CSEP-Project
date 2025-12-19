package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {
    Ingredient ingredient;
    Ingredient ingredient2;

    @BeforeEach
    void setUp(){
        ingredient = new Ingredient("fat", 50, 24, 45);
        ingredient2 = new Ingredient("fly", 50, 24, 45);
    }

    @Test
    void getName() {
        assertEquals(ingredient.getName(), "fat");
    }

    @Test
    void getFat() {
        assertEquals(ingredient.getFat(), 50);
    }

    @Test
    void getProtein() {
        assertEquals(ingredient.getProtein(), 24);
    }

    @Test
    void getCarbs() {
        assertEquals(ingredient.getCarbs(), 45);

    }

    @Test
    void setName() {
        ingredient.setName("Hello");
        assertEquals(ingredient, "Hello");
    }
}