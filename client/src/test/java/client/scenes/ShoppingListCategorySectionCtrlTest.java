package client.scenes;

import client.MyFXML;
import client.model.ShoppingListItem;
import commons.IngredientCategory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

import static com.sun.javafx.fxml.expression.Expression.set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShoppingListCategorySectionCtrlTest {

    private ShoppingListCategorySectionCtrl ctrl;
    private MyFXML fxml;
    private ResourceBundle bundle;
    private Function<Void, Void> onUpdate;
    private Function<ShoppingListItem, Void> onAddItem;
    private Function<ShoppingListItem, Void> onDeleteItem;

    @BeforeEach
    void setUp() {
        ctrl = new ShoppingListCategorySectionCtrl();

        // Manual JavaFX injection
        set(ctrl, "toggleButton", new Button());
        set(ctrl, "categoryLabel", new Label());
        set(ctrl, "itemsContainer", new VBox());

        fxml = mock(MyFXML.class);
        bundle = mock(ResourceBundle.class);
        onUpdate = mock(Function.class);
        onAddItem = mock(Function.class);
        onDeleteItem = mock(Function.class);
    }

    @Test
    void initialize() {
        ShoppingListItem item = mock(ShoppingListItem.class);
        when(item.isTextOnly()).thenReturn(false);

        ShoppingListElementCtrl elementCtrl = mock(ShoppingListElementCtrl.class);
        Parent parent = new VBox();
        when(fxml.load(ShoppingListElementCtrl.class, bundle,
                "client", "scenes", "ShoppingListElement.fxml"))
                .thenReturn(new Pair<>(elementCtrl, parent));

        List<ShoppingListItem> items = List.of(item);
        ctrl.initialize(IngredientCategory.FRUIT, items, fxml, bundle,
                onUpdate, onAddItem, onDeleteItem, false);

        assertEquals(IngredientCategory.FRUIT, ctrl.getCategory());
        assertEquals("Fruit", ctrl.getCategoryLabel().getText());
        assertEquals(1, ctrl.getItemsContainer().getChildren().size());
    }

    @Test
    void getCategory() {
        ctrl.initialize(IngredientCategory.VEGETABLES, new ArrayList<>(),
                fxml, bundle, onUpdate, onAddItem, onDeleteItem, false);
        assertEquals(IngredientCategory.VEGETABLES, ctrl.getCategory());
    }

    @Test
    void refreshItems() {
        ctrl.initialize(IngredientCategory.MEAT, new ArrayList<>(),
                fxml, bundle, onUpdate, onAddItem, onDeleteItem, false);
        assertEquals(0, ctrl.getItemsContainer().getChildren().size());

        ShoppingListItem item1 = mock(ShoppingListItem.class);
        when(item1.isTextOnly()).thenReturn(false);
        ShoppingListItem item2 = mock(ShoppingListItem.class);
        when(item2.isTextOnly()).thenReturn(true);

        ShoppingListElementCtrl elementCtrl = mock(ShoppingListElementCtrl.class);
        Parent parent = new VBox();
        when(fxml.load(ShoppingListElementCtrl.class, bundle,
                "client", "scenes", "ShoppingListElement.fxml"))
                .thenReturn(new Pair<>(elementCtrl, parent));

        List<ShoppingListItem> newItems = List.of(item1, item2);
        ctrl.refreshItems(newItems);

        assertEquals(2, ctrl.getItemsContainer().getChildren().size());
    }

    @Test
    void testToggle() {
        Button toggle = new Button();
        VBox container = new VBox();
        set(ctrl, "toggleButton", toggle);
        set(ctrl, "itemsContainer", container);

        assertTrue(container.isVisible());
        assertTrue(container.isManaged());

        assertFalse(container.isVisible());
        assertFalse(container.isManaged());
        assertEquals("▶", toggle.getText());


        assertTrue(container.isVisible());
        assertTrue(container.isManaged());
        assertEquals("▼", toggle.getText());
    }
}
