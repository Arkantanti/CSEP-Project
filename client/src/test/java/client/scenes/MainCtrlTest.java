package client.scenes;

import client.services.WebsocketService;
import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class MainCtrlTest {

    private MainCtrl sut;

    @BeforeEach
    public void setup() {
        WebsocketService ws = mock(WebsocketService.class);
        sut = new MainCtrl(ws);
    }

    @Test
    void showRecipe_throwsIfNotInitialized() {
        Recipe recipe = new Recipe("Test Recipe", 2, List.of("step 1"), "English",false,false,false);

        assertThrows(IllegalStateException.class,
                () -> sut.showRecipe(recipe),
                "Expected showRecipe to fail if FXML/AppViewCtrl are not initialized");
    }
}
