package client.scenes;

import commons.Ingredient;
import commons.Recipe;
import client.utils.FavoritesManager;

import commons.RecipeIngredient;
import commons.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import javafx.event.ActionEvent;


public class RecipeViewCtrlTest {

    private MainCtrl sut;
    private RecipeViewCtrl ctrl;

    @BeforeEach
    void setup() {
        ctrl = new RecipeViewCtrl(null, sut = new MainCtrl(), null, null);
    }

    private Ingredient ingredient(String name) {
        return new Ingredient(name, 0.0, 0.0, 0.0);
    }

    private RecipeIngredient ri(String ingredientName, String informalUnit, double amount, Unit unit) {
        return new RecipeIngredient(null, ingredient(ingredientName), informalUnit, amount, unit);
    }

    // --- Existing tests (kept) ---

    @Test
    void onFavoriteClicked_addsFavoriteWhenNotFavorited() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        AppViewCtrl appViewCtrl = mock(AppViewCtrl.class);

        // Must set up stub before creating controller, because constructor call getAppViewCtrl
        when(mainCtrl.getAppViewCtrl()).thenReturn(appViewCtrl);

        RecipeViewCtrl ctrl = new RecipeViewCtrl(null, mainCtrl, null, favoritesManager);

        Recipe recipe = new Recipe("Test Recipe", 2, List.of("step1"));
        recipe.setId(1L);

        when(favoritesManager.isFavorite(1L)).thenReturn(false);

        Field recipeField = RecipeViewCtrl.class.getDeclaredField("recipe");
        recipeField.setAccessible(true);
        recipeField.set(ctrl, recipe);

        Method method = RecipeViewCtrl.class.getDeclaredMethod("onFavoriteClicked");
        method.setAccessible(true);
        method.invoke(ctrl);

        verify(favoritesManager).addFavorite(1L);
        verify(appViewCtrl).loadRecipes();
    }

    @Test
    void onFavoriteClicked_removesFavoriteWhenFavorited() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        AppViewCtrl appViewCtrl = mock(AppViewCtrl.class);

       // Must set up stub before creating controller, because constructor calls getAppViewCtrl
        when(mainCtrl.getAppViewCtrl()).thenReturn(appViewCtrl);

        RecipeViewCtrl ctrl = new RecipeViewCtrl(null, mainCtrl, null, favoritesManager);

        Recipe recipe = new Recipe("Test Recipe", 2, List.of("step1"));
        recipe.setId(1L);

        when(favoritesManager.isFavorite(1L)).thenReturn(true);

        Field recipeField = RecipeViewCtrl.class.getDeclaredField("recipe");
        recipeField.setAccessible(true);
        recipeField.set(ctrl, recipe);

        Method method = RecipeViewCtrl.class.getDeclaredMethod("onFavoriteClicked");
        method.setAccessible(true);
        method.invoke(ctrl);

        verify(favoritesManager).removeFavorite(1L);
        verify(appViewCtrl).loadRecipes();
    }

    @Test
    void onFavoriteClicked_throwsWhenRecipeIsNull() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);

        when(mainCtrl.getAppViewCtrl()).thenReturn(null);

        RecipeViewCtrl ctrl = new RecipeViewCtrl(null, mainCtrl, null, favoritesManager);

        Field recipeField = RecipeViewCtrl.class.getDeclaredField("recipe");
        recipeField.setAccessible(true);
        recipeField.set(ctrl, null);

        Method method = RecipeViewCtrl.class.getDeclaredMethod("onFavoriteClicked");
        method.setAccessible(true);

        // wrapping exception in InvocationTargetException and later checking of it is of type IllegalStateException
        // because method.invoke() throws InvocationTargetException
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class,
                () -> method.invoke(ctrl));
        assertTrue(thrown.getCause() instanceof IllegalStateException);
    }

    @Test
    void onAddPreparationStepClicked_throwsWhenPreparationStepsIsNull() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        AppViewCtrl appViewCtrl = mock(AppViewCtrl.class);

        // must set up stub before creating controller, because constructor calls getAppViewCtrl()
        when(mainCtrl.getAppViewCtrl()).thenReturn(appViewCtrl);

        RecipeViewCtrl ctrl = new RecipeViewCtrl(null, mainCtrl, null, favoritesManager);

        Recipe recipe = new Recipe("Test Recipe", 1, null);

        Field recipeField = RecipeViewCtrl.class.getDeclaredField("recipe");
        recipeField.setAccessible(true);
        recipeField.set(ctrl, recipe);

        Method method = RecipeViewCtrl.class.getDeclaredMethod(
                "onAddPreparationStepClicked", ActionEvent.class);
        method.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class,
                () -> method.invoke(ctrl, (Object) null));
        assertTrue(thrown.getCause() instanceof IllegalStateException);
    }

    // --- Additional tests for broader coverage ---

    @Test
    void formatIngredient_informalUnit_hasHighestPriority_evenIfUnitOrAmountPresent() {
        RecipeIngredient sugar = ri("Sugar", "one pinch", 999.0, Unit.GRAM);

        String result = sugar.formatIngredient();

        // informal should override numeric formatting in this helper
        assertEquals("one pinch Sugar", result);
    }

    @Test
    void formatIngredient_informalUnit_whenUnitNull_stillUsesInformal() {
        RecipeIngredient sugar = ri("Sugar", "a handful", 123.0, null);

        String result = sugar.formatIngredient();

        assertEquals("a handful Sugar", result);
    }

    @Test
    void formatIngredient_informalUnit_emptyString_isIgnored_andFallsBackToFormalFormatting() {
        // Your helper uses: if (informalUnit != null && !informalUnit.isEmpty())
        // so "" should be treated as "not present"
        RecipeIngredient flour = ri("Flour", "", 100.0, Unit.GRAM);

        String result = flour.formatIngredient();;

        assertEquals("100.0g Flour", result);
    }

    @Test
    void formatIngredient_informalUnit_whitespace_isNotEmpty_soIsUsedVerbatim() {
        // Documents current behavior: isEmpty() does NOT treat "   " as empty
        RecipeIngredient salt = ri("Salt", "   ", 5.0, Unit.GRAM);

        String result = salt.formatIngredient();

        assertEquals("    Salt", result); // "   " + " " + "Salt"
    }

    @Test
    void formatIngredient_unknownUnit_hasEmptyUnitString_currentImplementation() {
        // Your helper only maps GRAM -> g and LITER -> L; everything else => ""
        // Use CUSTOM as representative of "not GRAM/LITER"
        RecipeIngredient pepper = ri("Pepper", null, 2.0, Unit.CUSTOM);

        String result = pepper.formatIngredient();

        assertEquals("2.0 Pepper", result);
    }

    @Test
    void formatIngredient_unitNull_hasEmptyUnitString_andStillFormatsAmount() {
        RecipeIngredient water = ri("Water", null, 3.0, null);

        String result = water.formatIngredient();

        assertEquals("3.0 Water", result);
    }

    @Test
    void formatIngredient_zeroAmount_isFormattedWithUnitIfKnown() {
        RecipeIngredient flour = ri("Flour", null, 0.0, Unit.GRAM);
        RecipeIngredient milk = ri("Milk", null, 0.0, Unit.LITER);

        assertEquals("0.0g Flour", flour.formatIngredient());
        assertEquals("0.0L Milk", milk.formatIngredient());
    }

    @Test
    void formatIngredient_negativeAmount_isFormattedVerbatim_noValidationHere() {
        // Documents current behavior: no validation in formatIngredient
        RecipeIngredient weird = ri("WeirdStuff", null, -12.5, Unit.GRAM);

        String result = weird.formatIngredient();

        assertEquals("-12.5g WeirdStuff", result);
    }

    @Test
    void formatIngredient_largeAmount_isFormattedVerbatim_noNormalizationInThisHelper() {
        // This helper does no normalization (normalization is now in RecipeIngredient.formatIngredientInternal)
        RecipeIngredient rice = ri("Rice", null, 2000.0, Unit.GRAM);

        String result = rice.formatIngredient();

        assertEquals("2000.0g Rice", result);
    }

    @Test
    void formatIngredient_ingredientNameWithSpaces_isPreserved() {
        RecipeIngredient brownSugar = ri("Brown Sugar", null, 50.0, Unit.GRAM);

        String result = brownSugar.formatIngredient();

        assertEquals("50.0g Brown Sugar", result);
    }

    @Test
    void formatIngredient_informalUnit_canContainNumbers_andIsPreservedVerbatim() {
        RecipeIngredient garlic = ri("Garlic", "2 cloves", 10.0, Unit.GRAM);

        String result = garlic.formatIngredient();

        assertEquals("2 cloves Garlic", result);
    }

    @Test
    void formatIngredient_hasSingleSpaceBetweenAmountUnitAndName() {
        RecipeIngredient flour = ri("Flour", null, 100.0, Unit.GRAM);

        String result = flour.formatIngredient();

        assertEquals("100.0g Flour", result);
        assertFalse(result.contains("  "));
    }
}
