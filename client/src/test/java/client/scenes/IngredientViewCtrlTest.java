package client.scenes;

import client.utils.ServerUtils;
import commons.Allergen;
import commons.Ingredient;
import commons.IngredientCategory;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IngredientViewCtrlTest {

    private IngredientViewCtrl ctrl;
    private ServerUtils server;
    private MainCtrl mainCtrl;
    private AppViewCtrl appViewCtrl;

    @BeforeEach
    void setUp() {
        server = mock(ServerUtils.class);
        mainCtrl = mock(MainCtrl.class);
        appViewCtrl = mock(AppViewCtrl.class);

        when(mainCtrl.getAppViewCtrl()).thenReturn(appViewCtrl);

        ctrl = new IngredientViewCtrl(server, mainCtrl);

        // Inject JavaFX controls manually
        ctrl.getCategoryComboBox().getItems().setAll(IngredientCategory.values());
    }

    @Test
    void initialize() {
        assertNotNull(ctrl.getProteinTf().getTextFormatter());
        assertNotNull(ctrl.getCarbsTf().getTextFormatter());
        assertNotNull(ctrl.getFatTf().getTextFormatter());
        assertNotNull(ctrl.getCategoryComboBox());
    }

    @Test
    void setIngredient() {
        Ingredient ingredient = new Ingredient(
                "TestIngredient",
                10.5,
                20.0,
                30.0,
                Set.of(Allergen.GLUTEN)
        );
        ingredient.setId(1L);
        ingredient.setCategory(IngredientCategory.FRUIT);

        when(server.recipeCount(1L)).thenReturn(3L);

        ctrl.setIngredient(ingredient, null);

        assertEquals("TestIngredient", ctrl.getNameLabel().getText());
        assertEquals("10.50", ctrl.getFatTf().getText());
        assertEquals("20.00", ctrl.getProteinTf().getText());
        assertEquals("30.00", ctrl.getCarbsTf().getText());
        assertEquals("3", ctrl.getUsedCountLabel().getText());
        assertEquals(IngredientCategory.FRUIT, ctrl.getCategoryComboBox().getValue());
        assertEquals("Fruit", ctrl.getCategoryLabel().getText());

        FlowPane allergensPane = ctrl.getHboxAllergens();
        assertEquals(1, allergensPane.getChildren().size());
    }

    @Test
    void deleteIngredient() {
        Ingredient ingredient = new Ingredient("Test", 1, 2, 3, Set.of());
        ingredient.setId(5L);

        ctrl.setIngredient(ingredient, null);

        when(server.recipeCount(5L)).thenReturn(0L);

        ctrl.deleteIngredient();

        verify(server).deleteIngredient(5L);
        verify(appViewCtrl).loadIngredients();
        verify(mainCtrl).showDefaultView();
    }

    @Test
    void onStopEditing() {
        Ingredient ingredient = new Ingredient("Test", 1, 2, 3, Set.of());
        ingredient.setId(1L);
        ctrl.setIngredient(ingredient, null);

        TextField proteinTf = ctrl.getProteinTf();
        proteinTf.setId("proteinTf");
        proteinTf.setText("12.345");

        ctrl.onStopEditing(proteinTf);

        assertEquals(12.35, ingredient.getProtein(), 0.001);
        verify(server).updateIngredient(ingredient);
        verify(appViewCtrl).loadIngredients();
    }

    @Test
    void addAllergen() {
        assertDoesNotThrow(() -> ctrl.addAllergen());
    }
}
