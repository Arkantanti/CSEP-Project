package client.scenes;

import client.utils.ServerUtils;
import commons.Allergen;
import commons.Ingredient;
import commons.IngredientCategory;
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
    }

    @Test
    void initialize() {
        assertDoesNotThrow(() -> {});
    }

    @Test
    void setIngredient() {
        Set<Allergen> allergens = Set.of(Allergen.GLUTEN, Allergen.MILK);
        Ingredient ingredient = new Ingredient("TestIngredient", 10.5, 20.3, 30.7, allergens);
        ingredient.setCategory(IngredientCategory.VEGETABLES);

        assertDoesNotThrow(() -> addIngredientCtrl.setIngredient(ingredient));
    }

    @Test
    void onStopEditingText() {
        assertDoesNotThrow(() -> addIngredientCtrl.onStopEditingText());
    }

    @Test
    void onStopEditingFloat() {
        assertDoesNotThrow(() -> {});
    }

    @Test
    void onCancel() {
        assertDoesNotThrow(() -> addIngredientCtrl.onCancel());
    }

    @Test
    void onSave() {
        Ingredient testIngredient = new Ingredient("Test", 1.0, 2.0, 3.0, Set.of());
        Ingredient savedIngredient = new Ingredient("Test", 1.0, 2.0, 3.0, Set.of());
        savedIngredient.setId(1L);

        when(serverUtils.addIngredient(any(Ingredient.class))).thenReturn(savedIngredient);

        assertDoesNotThrow(() -> addIngredientCtrl.onSave());
    }

    @Test
    void getIngredientSaved() {
        assertFalse(addIngredientCtrl.getIngredientSaved());
    }

    @Test
    void getIngredient() {
        assertDoesNotThrow(() -> addIngredientCtrl.getIngredient());
    }

    @Test
    void addAllergen() {
        assertDoesNotThrow(() -> addIngredientCtrl.addAllergen());
    }

}