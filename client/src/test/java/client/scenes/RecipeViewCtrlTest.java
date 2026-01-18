package client.scenes;

import client.services.NutrientsCalc;
import client.services.ShoppingListService;
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
        ShoppingListService sls = mock(ShoppingListService.class);
        NutrientsCalc nutrientsCalc = mock(NutrientsCalc.class);

        ServerUtils server = mock(ServerUtils.class);
        Printer printer = mock(Printer.class);

        return  new RecipeViewCtrl(server, mainCtrl, printer, favoritesManager, sls, nutrientsCalc);
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

        Recipe recipe = new Recipe("Test Recipe", 2, List.of("step1"),false,false,false);
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

        Recipe recipe = new Recipe("Test Recipe", 2, List.of("step1"),false,false,false);
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
        Recipe recipe = new Recipe("Test Recipe", 1, null,false,false,false);
        recipe.setId(1L);

        setField(ctrl, "recipe", recipe);

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> ctrl.onAddPreparationStepClicked((ActionEvent) null)
        );

        assertTrue(thrown.getMessage().toLowerCase().contains("preparation"));
    }

}
