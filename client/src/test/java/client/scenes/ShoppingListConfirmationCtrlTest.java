package client.scenes;

import client.MyFXML;
import client.services.ShoppingListService;
import commons.RecipeIngredient;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sun.javafx.fxml.expression.Expression.set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShoppingListConfirmationCtrlTest {

    private ShoppingListConfirmationCtrl ctrl;
    private MyFXML fxml;
    private ShoppingListService shoppingListService;
    private MainCtrl mainCtrl;
    private VBox ingredientListBox;
    private Stage stage;

    @BeforeEach
    void setUp() {
        shoppingListService = mock(ShoppingListService.class);
        mainCtrl = mock(MainCtrl.class);
        ctrl = new ShoppingListConfirmationCtrl(shoppingListService, mainCtrl);

        fxml = mock(MyFXML.class);
        ingredientListBox = new VBox();
        stage = mock(Stage.class);

        set(ctrl, "ingredientListBox", ingredientListBox);
        ctrl.initialize(fxml, stage);
    }

    @Test
    void initialize() {
        // initialize is already called in setUp, just test if fields are set
        assertNotNull(ctrl.getIngredientListBox());
    }

    @Test
    void loadList() {
        RecipeIngredient ingredient1 = mock(RecipeIngredient.class);
        RecipeIngredient ingredient2 = mock(RecipeIngredient.class);

        ctrl.loadList(List.of(ingredient1, ingredient2), 2.0, "Test Recipe");

        assertEquals(2, ctrl.getItems().size());
        assertEquals("Test Recipe", ctrl.getRecipeName());
    }

}
