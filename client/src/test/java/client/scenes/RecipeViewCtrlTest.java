package client.scenes;

import commons.Ingredient;
import commons.RecipeIngredient;
import commons.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecipeViewCtrlTest {

    private RecipeViewCtrl ctrl;

    @BeforeEach
    void setup() {
        ctrl = new RecipeViewCtrl(null, null, null);
    }

    private String format(RecipeIngredient ri) {
        try {
            Method m = RecipeViewCtrl.class
                    .getDeclaredMethod("formatIngredient", RecipeIngredient.class);
            m.setAccessible(true);
            return (String) m.invoke(ctrl, ri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void formatIngredient_usesInformalUnitWhenPresent() {
        Ingredient ingredient = new Ingredient("Sugar", 0.0, 0.0, 0.0);
        RecipeIngredient ri = new RecipeIngredient(null, ingredient, "one pinch", 0.0, null);

        String result = format(ri);

        assertEquals("one pinch Sugar", result);
    }

    @Test
    void formatIngredient_formatsGramUnit() {
        Ingredient ingredient = new Ingredient("Flour", 0.0, 0.0, 0.0);
        RecipeIngredient ri = new RecipeIngredient(null, ingredient, null, 100.0, Unit.GRAM);

        String result = format(ri);

        assertEquals("100.0g Flour", result);
    }

    @Test
    void formatIngredient_formatsLiterUnit() {
        Ingredient ingredient = new Ingredient("Milk", 0.0, 0.0, 0.0);
        RecipeIngredient ri = new RecipeIngredient(null, ingredient, null, 0.5, Unit.LITER);

        String result = format(ri);

        assertEquals("0.5L Milk", result);
    }
}
