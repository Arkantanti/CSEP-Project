package client.scenes;

import client.utils.ServerUtils;
import commons.Allergen;
import commons.Ingredient;
import commons.IngredientCategory;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddIngredientCtrlTest {

    private AddIngredientCtrl addIngredientCtrl;
    private ServerUtils serverUtils;
    private MainCtrl mainCtrl;
    private AppViewCtrl appViewCtrl;

    @BeforeEach
    void setUp() {
        serverUtils = mock(ServerUtils.class);
        mainCtrl = mock(MainCtrl.class);
        appViewCtrl = mock(AppViewCtrl.class);

        when(mainCtrl.getAppViewCtrl()).thenReturn(appViewCtrl);
        addIngredientCtrl = new AddIngredientCtrl(serverUtils, mainCtrl);

        addIngredientCtrl.initialize(false);
    }

    @Test
    void initialize() {
        assertEquals("NewIngredient", addIngredientCtrl.getNameTextField().getText());
        assertEquals("0.00", addIngredientCtrl.getFatTf().getText());
        assertEquals("0.00", addIngredientCtrl.getProteinTf().getText());
        assertEquals("0.00", addIngredientCtrl.getCarbsTf().getText());
        assertEquals(IngredientCategory.UNCATEGORIZED, addIngredientCtrl.getCategoryComboBox().getValue());

        assertNotNull(addIngredientCtrl.getIngredient());
        assertEquals("NewIngredient", addIngredientCtrl.getIngredient().getName());
        assertEquals(0.0, addIngredientCtrl.getIngredient().getFat(), 0.001);
        assertEquals(0.0, addIngredientCtrl.getIngredient().getProtein(), 0.001);
        assertEquals(0.0, addIngredientCtrl.getIngredient().getCarbs(), 0.001);
    }

    @Test
    void setIngredient() {
        Set<Allergen> allergens = Set.of(Allergen.GLUTEN, Allergen.MILK);
        Ingredient ingredient = new Ingredient("TestIngredient", 10.5, 20.3, 30.7, allergens);
        ingredient.setCategory(IngredientCategory.VEGETABLES);

        addIngredientCtrl.setIngredient(ingredient);

        assertEquals("TestIngredient", addIngredientCtrl.getNameTextField().getText());
        assertEquals("10.50", addIngredientCtrl.getFatTf().getText());
        assertEquals("20.30", addIngredientCtrl.getProteinTf().getText());
        assertEquals("30.70", addIngredientCtrl.getCarbsTf().getText());
        assertEquals(IngredientCategory.VEGETABLES, addIngredientCtrl.getCategoryComboBox().getValue());
        assertEquals(ingredient, addIngredientCtrl.getIngredient());
    }

    @Test
    void onStopEditingText() {
        addIngredientCtrl.getNameTextField().setText("");
        addIngredientCtrl.onStopEditingText();
        assertEquals("NewIngredient", addIngredientCtrl.getNameTextField().getText());
        assertEquals("NewIngredient", addIngredientCtrl.getIngredient().getName());

        addIngredientCtrl.getNameTextField().setText("New Name");
        addIngredientCtrl.onStopEditingText();
        assertEquals("New Name", addIngredientCtrl.getNameTextField().getText());
        assertEquals("New Name", addIngredientCtrl.getIngredient().getName());
    }

    @Test
    void onStopEditingFloat() {
        addIngredientCtrl.getFatTf().setText("");
        addIngredientCtrl.onStopEditingFloat(addIngredientCtrl.getFatTf());
        assertEquals("0", addIngredientCtrl.getFatTf().getText());
        assertEquals(0.0, addIngredientCtrl.getIngredient().getFat(), 0.001);

        addIngredientCtrl.getProteinTf().setText("12.345");
        addIngredientCtrl.onStopEditingFloat(addIngredientCtrl.getProteinTf());
        assertEquals("12.35", addIngredientCtrl.getProteinTf().getText());
        assertEquals(12.35, addIngredientCtrl.getIngredient().getProtein(), 0.001);

        addIngredientCtrl.getCarbsTf().setText("15");
        addIngredientCtrl.onStopEditingFloat(addIngredientCtrl.getCarbsTf());
        assertEquals("15.00", addIngredientCtrl.getCarbsTf().getText());
        assertEquals(15.0, addIngredientCtrl.getIngredient().getCarbs(), 0.001);
    }

    @Test
    void onCancel() {
        addIngredientCtrl.onCancel();
        verify(mainCtrl).showDefaultView();
        verifyNoMoreInteractions(mainCtrl);

        reset(mainCtrl);
        addIngredientCtrl.initialize(true);
        addIngredientCtrl.onCancel();
        verify(mainCtrl).closeAddIngredientWindow();
        verify(mainCtrl, never()).showDefaultView();
    }

    @Test
    void onSave() {
        Ingredient ingredient = new Ingredient("TestIngredient", 10.0, 20.0, 30.0, Set.of());
        addIngredientCtrl.setIngredient(ingredient);

        Ingredient savedIngredient = new Ingredient("TestIngredient", 10.0, 20.0, 30.0, Set.of());
        savedIngredient.setId(1L);

        when(serverUtils.addIngredient(any(Ingredient.class))).thenReturn(savedIngredient);

        addIngredientCtrl.getCategoryComboBox().setValue(IngredientCategory.FRUIT);
        addIngredientCtrl.onSave();

        verify(serverUtils).addIngredient(ingredient);
        verify(appViewCtrl).loadIngredients();
        verify(mainCtrl).showIngredient(savedIngredient);
        assertTrue(addIngredientCtrl.getIngredientSaved());
        assertEquals(savedIngredient, addIngredientCtrl.getIngredient());
        assertEquals(IngredientCategory.FRUIT, ingredient.getCategory());

        reset(serverUtils, appViewCtrl, mainCtrl);
        addIngredientCtrl.initialize(true);
        addIngredientCtrl.setIngredient(new Ingredient("Test2", 1, 2, 3, Set.of()));

        when(serverUtils.addIngredient(any(Ingredient.class))).thenReturn(ingredient);

        addIngredientCtrl.getCategoryComboBox().setValue(null);
        addIngredientCtrl.onSave();

        assertEquals(IngredientCategory.UNCATEGORIZED, addIngredientCtrl.getIngredient().getCategory());
        verify(mainCtrl).closeAddIngredientWindow();
        verify(appViewCtrl, never()).loadIngredients();
    }

    @Test
    void getIngredientSaved() {
        assertFalse(addIngredientCtrl.getIngredientSaved());

        addIngredientCtrl.setIngredient(new Ingredient("Test", 1, 2, 3, Set.of()));
        when(serverUtils.addIngredient(any(Ingredient.class))).thenReturn(addIngredientCtrl.getIngredient());
        addIngredientCtrl.onSave();

        assertTrue(addIngredientCtrl.getIngredientSaved());
    }

    @Test
    void getIngredient() {
        assertNotNull(addIngredientCtrl.getIngredient());

        Ingredient ingredient = new Ingredient("Test", 1, 2, 3, Set.of());
        addIngredientCtrl.setIngredient(ingredient);

        assertEquals(ingredient, addIngredientCtrl.getIngredient());
    }

    @Test
    void addAllergen() {
        assertDoesNotThrow(() -> addIngredientCtrl.addAllergen());
    }

    @Test
    void testTextFormatterPreventsInvalidInput() {
        TextField proteinTf = addIngredientCtrl.getProteinTf();
        TextFormatter<Double> formatter = (TextFormatter<Double>) proteinTf.getTextFormatter();

        assertNotNull(formatter);

        proteinTf.setText("abc");
        assertEquals("", proteinTf.getText());

        proteinTf.setText("12.34");
        assertEquals("12.34", proteinTf.getText());

        proteinTf.setText("12.34.56");
        assertEquals("12.34", proteinTf.getText());
    }
}