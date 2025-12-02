package client.utils;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrinterTest {

    private Recipe recipe;
    private Printer printer;

    @BeforeEach
    public void setup() {
        Ingredient i1 = new Ingredient("Butter",0,0,0);
        Ingredient i2 = new Ingredient("Bread",0,0,0);
        Ingredient i3 = new Ingredient("Cheese",0,0,0);
        recipe = new Recipe("Pancakes", 4, List.of("step1", "step2", "step3"));
        RecipeIngredient ri1 = new RecipeIngredient(recipe,i1,null,70, Unit.GRAM);
        RecipeIngredient ri2 = new RecipeIngredient(recipe,i2,"pinches",2, Unit.CUSTOM);
        RecipeIngredient ri3 = new RecipeIngredient(recipe,i3,"just a bit",0, Unit.CUSTOM);
        recipe.addIngredient(ri1);
        recipe.addIngredient(ri2);
        recipe.addIngredient(ri3);
        printer = new Printer();
    }

    @Test
    void recipePrint() {
        String expected = """
                ## Pancakes
                
                
                **Servings:** 4
                
                **Ingredients:**
                 - Butter - 70.0 GRAM
                 - Bread - 2.0 pinches
                 - Cheese -  just a bit
                
                **Preparation steps:**
                1. step1
                2. step2
                3. step3
                
                HAVE A GOOD MEAL!!""";
        assertEquals(expected,printer.recipePrint(recipe));
    }

    @Test
    void markdownToPDF(){
        String markdown = printer.recipePrint(recipe);
        // Get a random path to a temp file
        try {
            Path dir = Files.createTempDirectory("config-test");
            Path tmp = dir.resolve("anotherDirectory", "file.pdf");
            printer.markdownToPDF(tmp,markdown);
            // File exists
            assertTrue(Files.exists(tmp), "File does not exist");
            // File is readable
            assertTrue(Files.isReadable(tmp), "File is not readable");
            // File is pdf
            byte[] header = new byte[4];
            try (InputStream in = Files.newInputStream(tmp)) {
                in.read(header);
            }

            String headerStr = new String(header, StandardCharsets.ISO_8859_1);
            assertEquals("%PDF", headerStr, "File is not a valid PDF file");

        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
    }
}