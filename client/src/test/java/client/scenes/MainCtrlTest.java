package client.scenes;

import client.utils.FavoritesPollingService;
import commons.Language;
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
        FavoritesPollingService pollingService = mock(FavoritesPollingService.class);
        sut = new MainCtrl(ws, pollingService);
    }

    @Test
    void showRecipe_throwsIfNotInitialized() {
        Recipe recipe = new Recipe("Test Recipe", 2, List.of("step 1"), Language.English,false,false,false);

        assertThrows(IllegalStateException.class,
                () -> sut.showRecipe(recipe),
                "Expected showRecipe to fail if FXML/AppViewCtrl are not initialized");
    }
}
