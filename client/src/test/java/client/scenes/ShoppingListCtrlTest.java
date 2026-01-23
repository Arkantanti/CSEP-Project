package client.scenes;

import client.MyFXML;
import client.model.ShoppingListItem;
import client.services.ShoppingListService;
import client.utils.Printer;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.sun.javafx.fxml.expression.Expression.set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShoppingListCtrlTest {

    private ShoppingListCtrl ctrl;
    private MyFXML fxml;
    private ShoppingListService shoppingListService;
    private Printer printer;
    private MainCtrl mainCtrl;
    private VBox ingredientListBox;
    private Button addIngredientButton;
    private Button addTextButton;
    private Button toggleGroupingButton;
    private HBox addModeToggle;

    @BeforeEach
    void setUp() {
        shoppingListService = mock(ShoppingListService.class);
        printer = mock(Printer.class);
        mainCtrl = mock(MainCtrl.class);
        fxml = mock(MyFXML.class);

        ingredientListBox = new VBox();
        addIngredientButton = new Button();
        addTextButton = new Button();
        toggleGroupingButton = new Button();
        addModeToggle = new HBox();

        ctrl = new ShoppingListCtrl(shoppingListService, mainCtrl, printer);
        set(ctrl, "ingredientListBox", ingredientListBox);
        set(ctrl, "addIngredientButton", addIngredientButton);
        set(ctrl, "addTextButton", addTextButton);
        set(ctrl, "toggleGroupingButton", toggleGroupingButton);
        set(ctrl, "addModeToggle", addModeToggle);

        ctrl.initialize(fxml, null);
    }

    @Test
    void testLoadShoppingListGrouped() {
        // Mock shopping list items
        ShoppingListItem item1 = mock(ShoppingListItem.class);
        ShoppingListItem item2 = mock(ShoppingListItem.class);
        when(shoppingListService.getShoppingList()).thenReturn(List.of(item1, item2));
        when(shoppingListService.getCategoryForItem(item1)).thenReturn(item1.isTextOnly() ? null : null);
        when(shoppingListService.getCategoryForItem(item2)).thenReturn(item2.isTextOnly() ? null : null);

        // Mock FXML load
        ShoppingListCategorySectionCtrl sectionCtrl = mock(ShoppingListCategorySectionCtrl.class);
        Parent parent = new VBox();

        ctrl.loadShoppingList();

        // Should add sections to ingredientListBox
        assertFalse(ctrl.getIngredientListBox().getChildren().isEmpty());
        verify(sectionCtrl, atLeastOnce()).initialize(any(), any(), any(), any(), any(), any(), any(), anyBoolean());
    }

    @Test
    void testOnAddShoppingListElement() {
        ShoppingListElementCtrl elementCtrl = mock(ShoppingListElementCtrl.class);
        Parent parent = new VBox();

        ctrl.onAddShoppingListElement();

        verify(elementCtrl).startEditingFromCtrl();
        assertEquals(1, ingredientListBox.getChildren().size());
    }

    @Test
    void testClear() {
        ctrl.clear();
        verify(shoppingListService).clear();
        assertTrue(ingredientListBox.getChildren().isEmpty());
    }


    @Test
    void testPrint() throws IOException {
        Path path = Path.of("ShoppingList.pdf");
        when(mainCtrl.showFileChooser("ShoppingList.pdf")).thenReturn(path);
        when(shoppingListService.getShoppingList()).thenReturn(List.of());

        ctrl.print();

        verify(printer).createShoppingListOutputString(anyList(), eq(shoppingListService));
        verify(printer).markdownToPDF(eq(path), anyString());
    }

}
