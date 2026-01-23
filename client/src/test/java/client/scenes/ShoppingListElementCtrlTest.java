package client.scenes;

import client.model.ShoppingListItem;
import client.services.IngredientService;
import client.utils.ServerUtils;
import commons.Ingredient;
import commons.Unit;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static com.sun.javafx.fxml.expression.Expression.set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShoppingListElementCtrlTest {

    private ShoppingListElementCtrl ctrl;
    private ServerUtils serverUtils;
    private IngredientService ingredientService;

    private ShoppingListItem item;
    private HBox defaultView;
    private HBox editView;
    private TextField amountField;
    private ComboBox<Unit> unitComboBox;
    private ComboBox<Ingredient> ingredientComboBox;

    private Function<Void, Void> onUpdate;
    private Function<ShoppingListItem, Void> onAddIngredient;
    private Function<ShoppingListItem, Void> onDeleteIngredient;

    @BeforeEach
    void setUp() {
        serverUtils = mock(ServerUtils.class);
        ingredientService = mock(IngredientService.class);

        ctrl = new ShoppingListElementCtrl(serverUtils, ingredientService);

        defaultView = new HBox();
        editView = new HBox();
        amountField = new TextField();
        unitComboBox = new ComboBox<>();
        ingredientComboBox = new ComboBox<>();

        set(ctrl, "defaultView", defaultView);
        set(ctrl, "editView", editView);
        set(ctrl, "amountField", amountField);
        set(ctrl, "unitComboBox", unitComboBox);
        set(ctrl, "ingredientComboBox", ingredientComboBox);

        item = new ShoppingListItem("Test Item");

        onUpdate = mock(Function.class);
        onAddIngredient = mock(Function.class);
        onDeleteIngredient = mock(Function.class);
    }

    @Test
    void testInitializeTextMode() {
        ctrl.initialize(item, onUpdate, onAddIngredient, onDeleteIngredient, true);
        assertTrue(defaultView.isVisible());
        assertFalse(editView.isVisible());
        assertEquals("Test Item", ctrl.getTextLabel().getText());
    }

    @Test
    void testEnterEditModeSetsViewsCorrectly() {
        ctrl.initialize(item, onUpdate, onAddIngredient, onDeleteIngredient, true);
        ctrl.enterEditMode();
        assertTrue(editView.isVisible());
        assertFalse(defaultView.isVisible());
    }

    @Test
    void testStartEditingFromCtrl() {
        ctrl.initialize(null, onUpdate, onAddIngredient, onDeleteIngredient, true);
        ctrl.startEditingFromCtrl();
        assertTrue(editView.isVisible());
        assertFalse(defaultView.isVisible());
        assertEquals("Enter text", amountField.getPromptText());
    }

    @Test
    void testConfirmTextItemAddsNewItem() {
        ShoppingListItem newItem = null;
        ctrl.initialize(newItem, onUpdate, onAddIngredient, onDeleteIngredient, true);

        amountField.setText("New Text Item");

        verify(onAddIngredient).apply(any());
        assertTrue(defaultView.isVisible());
        assertFalse(editView.isVisible());
    }

    @Test
    void testCancelResetsViews() {
        ctrl.initialize(item, onUpdate, onAddIngredient, onDeleteIngredient, true);
        assertTrue(defaultView.isVisible());
        assertFalse(editView.isVisible());
        verify(onUpdate).apply(null);
    }
}
