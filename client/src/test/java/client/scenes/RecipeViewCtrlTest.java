package client.scenes;

import client.utils.FavoritesManager;
import client.utils.Printer;
import client.utils.ServerUtils;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecipeViewCtrlTest {

    private static RecipeViewCtrl newCtrl(MainCtrl mainCtrl, FavoritesManager favoritesManager) {
        // Constructor stores appViewCtrl immediately, so stub before creating controller
        AppViewCtrl appViewCtrl = mock(AppViewCtrl.class);
        when(mainCtrl.getAppViewCtrl()).thenReturn(appViewCtrl);

        ServerUtils server = mock(ServerUtils.class);
        Printer printer = mock(Printer.class);

        return new RecipeViewCtrl(server, mainCtrl, printer, favoritesManager);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static Object invokePrivate(Object target, String methodName, Class<?>... paramTypes) throws Exception {
        Method m = target.getClass().getDeclaredMethod(methodName, paramTypes);
        m.setAccessible(true);
        return m.invoke(target);
    }

    @Test
    void onFavoriteClicked_addsFavoriteWhenNotFavorited() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);

        RecipeViewCtrl ctrl = newCtrl(mainCtrl, favoritesManager);

        Recipe recipe = new Recipe("Test Recipe", 2, List.of("step1"));
        recipe.setId(1L);

        when(favoritesManager.isFavorite(1L)).thenReturn(false);

        // Avoid JavaFX toolkit issues: keep favoriteButton null (updateFavoriteButton() will just return)
        setField(ctrl, "recipe", recipe);

        invokePrivate(ctrl, "onFavoriteClicked");

        verify(favoritesManager).addFavorite(1L);
        verify(favoritesManager, never()).removeFavorite(anyLong());

        AppViewCtrl appViewCtrl = mainCtrl.getAppViewCtrl();
        verify(appViewCtrl).loadRecipes();
    }

    @Test
    void onFavoriteClicked_removesFavoriteWhenFavorited() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);

        RecipeViewCtrl ctrl = newCtrl(mainCtrl, favoritesManager);

        Recipe recipe = new Recipe("Test Recipe", 2, List.of("step1"));
        recipe.setId(1L);

        when(favoritesManager.isFavorite(1L)).thenReturn(true);

        setField(ctrl, "recipe", recipe);

        invokePrivate(ctrl, "onFavoriteClicked");

        verify(favoritesManager).removeFavorite(1L);
        verify(favoritesManager, never()).addFavorite(anyLong());

        AppViewCtrl appViewCtrl = mainCtrl.getAppViewCtrl();
        verify(appViewCtrl).loadRecipes();
    }

    @Test
    void onFavoriteClicked_throwsWhenRecipeIsNull() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);

        RecipeViewCtrl ctrl = newCtrl(mainCtrl, favoritesManager);

        setField(ctrl, "recipe", null);

        InvocationTargetException thrown = assertThrows(
                InvocationTargetException.class,
                () -> invokePrivate(ctrl, "onFavoriteClicked")
        );

        assertInstanceOf(IllegalStateException.class, thrown.getCause());
        assertTrue(thrown.getCause().getMessage().toLowerCase().contains("recipe"));
    }

    @Test
    void onAddPreparationStepClicked_throwsWhenPreparationStepsIsNull() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);

        RecipeViewCtrl ctrl = newCtrl(mainCtrl, favoritesManager);

        // This must throw BEFORE touching fxml or JavaFX containers
        Recipe recipe = new Recipe("Test Recipe", 1, null);
        recipe.setId(1L);

        setField(ctrl, "recipe", recipe);

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> ctrl.onAddPreparationStepClicked((ActionEvent) null)
        );

        assertTrue(thrown.getMessage().toLowerCase().contains("preparation"));
    }

    @Test
    void calculateCaloriesForRecipe_returnsZeroWhenTotalMassIsZero() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        RecipeViewCtrl ctrl = newCtrl(mainCtrl, favoritesManager);

        // ingredients list where everything is CUSTOM -> ignored -> totalMass stays 0
        RecipeIngredient ri = mock(RecipeIngredient.class);
        when(ri.getUnit()).thenReturn(Unit.CUSTOM);
        when(ri.getIngredient()).thenReturn(mock(Ingredient.class));
        when(ri.getAmount()).thenReturn(123.0);

        setField(ctrl, "ingredients", List.of(ri));
        setField(ctrl, "baseServings", 2);
        setField(ctrl, "targetServings", 2.0);

        double result = (double) invokePrivate(ctrl, "calculateCaloriesForRecipe");
        assertEquals(0.0, result, 1e-9);
    }

    @Test
    void calculateCaloriesForRecipe_combinesGramAndNonGramUsingTimesTenRule() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        RecipeViewCtrl ctrl = newCtrl(mainCtrl, favoritesManager);

        // GRAM ingredient: calories=2, amount=50 => adds 100 calories, 50 mass
        Ingredient ingGram = mock(Ingredient.class);
        when(ingGram.calculateCalories()).thenReturn(2.0);
        RecipeIngredient riGram = mock(RecipeIngredient.class);
        when(riGram.getIngredient()).thenReturn(ingGram);
        when(riGram.getUnit()).thenReturn(Unit.GRAM);
        when(riGram.getAmount()).thenReturn(50.0);

        // Non-gram ingredient: calories=3, amount=1 => adds 3*1*10=30 calories, 1*10=10 mass
        Ingredient ingNonGram = mock(Ingredient.class);
        when(ingNonGram.calculateCalories()).thenReturn(3.0);
        RecipeIngredient riNonGram = mock(RecipeIngredient.class);
        when(riNonGram.getIngredient()).thenReturn(ingNonGram);
        // pick any non-CUSTOM, non-GRAM unit your enum has; LITER is typical
        when(riNonGram.getUnit()).thenReturn(Unit.LITER);
        when(riNonGram.getAmount()).thenReturn(1.0);

        setField(ctrl, "ingredients", List.of(riGram, riNonGram));
        setField(ctrl, "baseServings", 2);
        setField(ctrl, "targetServings", 2.0); // factor = 1

        double result = (double) invokePrivate(ctrl, "calculateCaloriesForRecipe");

        double expected = 130.0 / 60.0; // (100+30) / (50+10)
        assertEquals(expected, result, 1e-9);
    }

    @Test
    void calculateCaloriesForRecipe_appliesServingsFactorToResult() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        RecipeViewCtrl ctrl = newCtrl(mainCtrl, favoritesManager);

        Ingredient ing = mock(Ingredient.class);
        when(ing.calculateCalories()).thenReturn(10.0);

        RecipeIngredient ri = mock(RecipeIngredient.class);
        when(ri.getIngredient()).thenReturn(ing);
        when(ri.getUnit()).thenReturn(Unit.GRAM);
        when(ri.getAmount()).thenReturn(100.0);

        setField(ctrl, "ingredients", List.of(ri));
        setField(ctrl, "baseServings", 2);
        setField(ctrl, "targetServings", 4.0); // factor = 2

        double result = (double) invokePrivate(ctrl, "calculateCaloriesForRecipe");

        // totalCalories = 10*100 = 1000, totalMass=100, ratio=10, factor=2 => 20
        assertEquals(20.0, result, 1e-9);
    }

    @Test
    void caloriesDisplay_format_matchesUpdateLogic_withoutJavaFxToolkit() throws Exception {
        FavoritesManager favoritesManager = mock(FavoritesManager.class);
        MainCtrl mainCtrl = mock(MainCtrl.class);
        RecipeViewCtrl ctrl = newCtrl(mainCtrl, favoritesManager);

        Ingredient ing = mock(Ingredient.class);
        when(ing.calculateCalories()).thenReturn(1.0);

        RecipeIngredient ri = mock(RecipeIngredient.class);
        when(ri.getIngredient()).thenReturn(ing);
        when(ri.getUnit()).thenReturn(Unit.GRAM);
        when(ri.getAmount()).thenReturn(3.0); // totalCalories=3, totalMass=3 => 1.0

        setField(ctrl, "ingredients", List.of(ri));
        setField(ctrl, "baseServings", 1);
        setField(ctrl, "targetServings", 1.0);

        double kcalPer100g = (double) invokePrivate(ctrl, "calculateCaloriesForRecipe");

        String text = ((int) kcalPer100g) + " kcal/100g";
        assertEquals("1 kcal/100g", text);
    }


}
