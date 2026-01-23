package client.scenes;

import client.services.RecipeService;
import client.utils.ServerUtils;
import commons.Language;
import commons.Recipe;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sun.javafx.fxml.expression.Expression.set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddRecipeCtrlTest {

    private AddRecipeCtrl ctrl;
    private ServerUtils server;
    private MainCtrl mainCtrl;
    private AppViewCtrl appViewCtrl;
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        server = mock(ServerUtils.class);
        mainCtrl = mock(MainCtrl.class);
        appViewCtrl = mock(AppViewCtrl.class);
        recipeService = mock(RecipeService.class);

        when(mainCtrl.getAppViewCtrl()).thenReturn(appViewCtrl);
        when(recipeService.getAllRecipes()).thenReturn(List.of());

        ctrl = new AddRecipeCtrl(server, mainCtrl, recipeService);

        // Inject JavaFX controls manually
        set(ctrl, "nameTextField", new TextField());
        set(ctrl, "servingsArea", new TextField());
        set(ctrl, "preparationsArea", new TextArea());
        set(ctrl, "ingredientsContainer", new VBox());
        set(ctrl, "languageChoise", new ComboBox<>());
        set(ctrl, "cheapCheckBox", new CheckBox());
        set(ctrl, "fastCheckBox", new CheckBox());
        set(ctrl, "veganCheckBox", new CheckBox());
    }

    @Test
    void initialize() {
        ctrl.initialize(null);

        assertNull(ctrl.getRecipe());
        assertFalse(ctrl.getIsSaved());
    }

    @Test
    void onSaveRecipe() {
        ctrl.initialize(null);

        ctrl.getNameTextField().setText("Test Recipe");
        ctrl.getServingsArea().setText("2");
        ctrl.getPreparationsArea().setText("Step 1");
        ctrl.getLanguageChoise().getItems().setAll("English");
        ctrl.getLanguageChoise().setValue("English");

        Recipe saved = new Recipe("Test Recipe", 2, List.of("Step 1"),
                Language.English, false, false, false);
        saved.setId(1L);

        when(server.add(any(Recipe.class))).thenReturn(saved);

        ctrl.onSaveRecipe();

        verify(server).add(any(Recipe.class));
        verify(appViewCtrl).loadRecipes();
        verify(mainCtrl).showRecipe(saved);

        assertTrue(ctrl.getIsSaved());
        assertEquals(saved, ctrl.getRecipe());
    }

    @Test
    void testClone() {
        Recipe original = new Recipe(
                "Original",
                3,
                List.of("Step A", "Step B"),
                Language.English,
                true,
                false,
                true
        );
        original.setId(10L);

        ctrl.initialize(null);
        ctrl.clone(original);

        Recipe cloned = ctrl.getRecipe();

        assertNotNull(cloned);
        assertEquals("Original - Clone", cloned.getName());
        assertEquals(3, cloned.getServings());
        assertEquals(Language.English, cloned.getLanguage());
        assertTrue(cloned.isCheap());
        assertTrue(cloned.isVegan());
    }

    @Test
    void onCancel() {
        Recipe recipe = new Recipe("Test", 1, List.of("Step"), Language.English,false,false,false);
        recipe.setId(5L);

        set(ctrl, "recipe", recipe);
        when(mainCtrl.getFirstOpen()).thenReturn(false);

        ctrl.onCancel();

        verify(server).deleteRecipe(5L);
        verify(mainCtrl).showAppView();
        verify(appViewCtrl).loadRecipes();
    }

    @Test
    void deleter() {
        ctrl.deleter(7L);
        verify(server).deleteRecipe(7L);
    }

    @Test
    void getIsSaved() {
        assertFalse(ctrl.getIsSaved());
    }

    @Test
    void setIsSavedTrue() {
        ctrl.setIsSavedTrue();
        assertTrue(ctrl.getIsSaved());
    }

    @Test
    void getRecipe() {
        assertNull(ctrl.getRecipe());

        Recipe recipe = new Recipe("Test", 1, List.of("Step"), Language.English,false,false,false);
        set(ctrl, "recipe", recipe);

        assertEquals(recipe, ctrl.getRecipe());
    }
}
