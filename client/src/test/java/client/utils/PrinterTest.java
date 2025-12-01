package client.utils;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrinterTest {

    private Recipe recipe;

    @BeforeEach
    public void setup() {
        Ingredient i1 = new Ingredient("Butter",0,0,0);
        Ingredient i2 = new Ingredient("Bread",0,0,0);
        Ingredient i3 = new Ingredient("Cheese",0,0,0);
        Recipe r = new Recipe("Pancakes", 4, List.of("step1", "step2", "step3"));
    }

    @Test
    void recipePrint() {

    }

    @Test
    void markdownToPDF() {
    }
}