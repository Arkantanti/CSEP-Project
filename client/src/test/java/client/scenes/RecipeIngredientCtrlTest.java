package client.scenes;

import client.services.IngredientService;
import client.utils.ServerUtils;
import commons.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.sun.javafx.fxml.expression.Expression.set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeIngredientCtrlTest {

    private RecipeIngredientCtrl ctrl;
    private ServerUtils serverUtils;
    private IngredientService ingredientService;
    private MainCtrl mainCtrl;

    private Recipe recipe;
    private RecipeIngredient recipeIngredient;

    @BeforeEach
    void setUp() {
        serverUtils = mock(ServerUtils.class);
        ingredientService = mock(IngredientService.class);
        mainCtrl = mock(MainCtrl.class);

        ctrl = new RecipeIngredientCtrl(serverUtils, mainCtrl, ingredientService);

        // Inject JavaFX fields manually
        set(ctrl, "defaultView", new HBox());
        set(ctrl, "editView", new HBox());
        set(ctrl, "textLabel", new Label());
        set(ctrl, "amountField", new TextField());
        set(ctrl, "unitComboBox", new ComboBox<Unit>());
        set(ctrl, "ingredientComboBox", new ComboBox<Ingredient>());

        recipe = new Recipe("Test", 1, List.of("step"), Language.English,false,false,false);
        recipe.setId(1L);

        Ingredient ingredient = new Ingredient("Sugar", 0, 0, 100, Set.of());
        recipeIngredient = new RecipeIngredient(recipe, ingredient, null, 10, Unit.GRAM);
        recipeIngredient.setId(5L);
    }

    @Test
    void initialize() {
        Runnable updater = mock(Runnable.class);

        ctrl.initialize(recipeIngredient, recipe, updater);

        assertTrue(ctrl.getDefaultView().isVisible());
        assertFalse(ctrl.getEditView().isVisible());
        assertEquals(recipeIngredient.formatIngredient(), ctrl.getTextLabel().getText());
    }

    @Test
    void onEditClicked() throws Exception {
        ctrl.initialize(recipeIngredient, recipe, () -> {});

        when(ingredientService.getAllIngredients())
                .thenReturn(List.of(recipeIngredient.getIngredient()));

        assertTrue(ctrl.getEditView().isVisible());
        assertFalse(ctrl.getDefaultView().isVisible());

        assertEquals("10.0", ctrl.getAmountField().getText());
        assertEquals(Unit.GRAM, ctrl.getUnitComboBox().getValue());
    }

    @Test
    void onDeleteClicked_localRecipe() throws Exception {
        recipe.setId(0L);
        recipe.setRecipeIngredients(new ArrayList<>(List.of(recipeIngredient)));

        ctrl.initialize(recipeIngredient, recipe, () -> {});

        assertTrue(recipe.getRecipeIngredients().isEmpty());
    }

    @Test
    void onDeleteClicked_serverRecipe() throws Exception {
        ctrl.initialize(recipeIngredient, recipe, () -> {});

        verify(serverUtils).deleteRecipeIngredient(5L);
    }

    @Test
    void onConfirmClicked_updatesExistingIngredient() throws Exception {
        ctrl.initialize(recipeIngredient, recipe, () -> {});

        ctrl.getAmountField().setText("25");
        ctrl.getUnitComboBox().getItems().setAll(Unit.values());
        ctrl.getUnitComboBox().getSelectionModel().select(Unit.GRAM);

        Ingredient newIngredient = new Ingredient("Flour", 0, 0, 80, Set.of());
        ctrl.getIngredientComboBox().getItems().setAll(newIngredient);
        ctrl.getIngredientComboBox().getSelectionModel().select(newIngredient);


        assertEquals(25, recipeIngredient.getAmount());
        assertEquals(Unit.GRAM, recipeIngredient.getUnit());
        assertEquals(newIngredient, recipeIngredient.getIngredient());

        verify(serverUtils).updateRecipeIngredient(recipeIngredient);
    }

    @Test
    void applyScaleFactor() {
        ctrl.initialize(recipeIngredient, recipe, () -> {});
        ctrl.applyScaleFactor(2);

        assertTrue(ctrl.getTextLabel().getText().contains("20"));
    }

    @Test
    void startEditingFromCtrl() {
        when(ingredientService.getAllIngredients()).thenReturn(List.of());

        ctrl.startEditingFromCtrl();

        assertTrue(ctrl.getEditView().isVisible());
        assertFalse(ctrl.getDefaultView().isVisible());
        assertEquals(Unit.GRAM, ctrl.getUnitComboBox().getValue());
    }
}
