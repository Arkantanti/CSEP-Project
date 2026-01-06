package client.scenes;

import commons.Recipe;
import client.utils.FavoritesManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javafx.event.ActionEvent;


public class RecipeViewCtrlTest {

    private MainCtrl sut;
    private RecipeViewCtrl ctrl;

    @BeforeEach
    void setup() {
        ctrl = new RecipeViewCtrl(null, sut = new MainCtrl(), null, null);
    }

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
}
