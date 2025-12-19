package client.scenes;

import commons.Ingredient;
import commons.RecipeIngredient;
import commons.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeViewCtrlTest {

    private MainCtrl sut;
    private RecipeViewCtrl ctrl;

    @BeforeEach
    void setup() {
        ctrl = new RecipeViewCtrl(null, sut = new MainCtrl(), null);
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

    private Ingredient ingredient(String name) {
        return new Ingredient(name, 0.0, 0.0, 0.0);
    }

    private RecipeIngredient ri(String ingredientName, String informalUnit, double amount, Unit unit) {
        return new RecipeIngredient(null, ingredient(ingredientName), informalUnit, amount, unit);
    }

    // --- Existing tests (kept) ---

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

    // --- Additional tests for broader coverage ---

    @Test
    void formatIngredient_informalUnit_hasHighestPriority_evenIfUnitOrAmountPresent() {
        RecipeIngredient sugar = ri("Sugar", "one pinch", 999.0, Unit.GRAM);

        String result = format(sugar);

        // informal should override numeric formatting in this helper
        assertEquals("one pinch Sugar", result);
    }

    @Test
    void formatIngredient_informalUnit_whenUnitNull_stillUsesInformal() {
        RecipeIngredient sugar = ri("Sugar", "a handful", 123.0, null);

        String result = format(sugar);

        assertEquals("a handful Sugar", result);
    }

    @Test
    void formatIngredient_informalUnit_emptyString_isIgnored_andFallsBackToFormalFormatting() {
        // Your helper uses: if (informalUnit != null && !informalUnit.isEmpty())
        // so "" should be treated as "not present"
        RecipeIngredient flour = ri("Flour", "", 100.0, Unit.GRAM);

        String result = format(flour);

        assertEquals("100.0g Flour", result);
    }

    @Test
    void formatIngredient_informalUnit_whitespace_isNotEmpty_soIsUsedVerbatim() {
        // Documents current behavior: isEmpty() does NOT treat "   " as empty
        RecipeIngredient salt = ri("Salt", "   ", 5.0, Unit.GRAM);

        String result = format(salt);

        assertEquals("    Salt", result); // "   " + " " + "Salt"
    }

    @Test
    void formatIngredient_unknownUnit_hasEmptyUnitString_currentImplementation() {
        // Your helper only maps GRAM -> g and LITER -> L; everything else => ""
        // Use CUSTOM as representative of "not GRAM/LITER"
        RecipeIngredient pepper = ri("Pepper", null, 2.0, Unit.CUSTOM);

        String result = format(pepper);

        assertEquals("2.0 Pepper", result);
    }

    @Test
    void formatIngredient_unitNull_hasEmptyUnitString_andStillFormatsAmount() {
        RecipeIngredient water = ri("Water", null, 3.0, null);

        String result = format(water);

        assertEquals("3.0 Water", result);
    }

    @Test
    void formatIngredient_zeroAmount_isFormattedWithUnitIfKnown() {
        RecipeIngredient flour = ri("Flour", null, 0.0, Unit.GRAM);
        RecipeIngredient milk = ri("Milk", null, 0.0, Unit.LITER);

        assertEquals("0.0g Flour", format(flour));
        assertEquals("0.0L Milk", format(milk));
    }

    @Test
    void formatIngredient_negativeAmount_isFormattedVerbatim_noValidationHere() {
        // Documents current behavior: no validation in formatIngredient
        RecipeIngredient weird = ri("WeirdStuff", null, -12.5, Unit.GRAM);

        String result = format(weird);

        assertEquals("-12.5g WeirdStuff", result);
    }

    @Test
    void formatIngredient_largeAmount_isFormattedVerbatim_noNormalizationInThisHelper() {
        // This helper does no normalization (normalization is now in RecipeIngredient.formatIngredientInternal)
        RecipeIngredient rice = ri("Rice", null, 2000.0, Unit.GRAM);

        String result = format(rice);

        assertEquals("2000.0g Rice", result);
    }

    @Test
    void formatIngredient_ingredientNameWithSpaces_isPreserved() {
        RecipeIngredient brownSugar = ri("Brown Sugar", null, 50.0, Unit.GRAM);

        String result = format(brownSugar);

        assertEquals("50.0g Brown Sugar", result);
    }

    @Test
    void formatIngredient_informalUnit_canContainNumbers_andIsPreservedVerbatim() {
        RecipeIngredient garlic = ri("Garlic", "2 cloves", 10.0, Unit.GRAM);

        String result = format(garlic);

        assertEquals("2 cloves Garlic", result);
    }

    @Test
    void formatIngredient_hasSingleSpaceBetweenAmountUnitAndName() {
        RecipeIngredient flour = ri("Flour", null, 100.0, Unit.GRAM);

        String result = format(flour);

        assertEquals("100.0g Flour", result);
        assertFalse(result.contains("  "));
    }
}
