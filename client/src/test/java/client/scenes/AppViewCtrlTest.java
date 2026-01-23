package client.scenes;

import client.services.IngredientService;
import client.services.RecipeService;
import client.utils.FavoritesManager;
import client.utils.PreferenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
public class AppViewCtrlTest {

    private AppViewCtrl ctrl;

    @Mock private MainCtrl mainCtrl;
    @Mock private FavoritesManager favoritesManager;
    @Mock private RecipeService recipeService;
    @Mock private IngredientService ingredientService;
    @Mock private PreferenceManager preferenceManager;

    @BeforeEach
    void setup() {
        ctrl = new AppViewCtrl(mainCtrl, favoritesManager, recipeService, ingredientService, preferenceManager);
    }

    @Test
    void constructor_storesMainCtrlReference() {
        try {
            Field f = AppViewCtrl.class.getDeclaredField("mainCtrl");
            f.setAccessible(true);
            Object value = f.get(ctrl);
            assertSame(mainCtrl, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}