package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import commons.Showable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AppViewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private StackPane contentRoot;

    @FXML
    private ListView<Showable> itemsList;

    @FXML
    private Button recipesButton;

    @FXML
    private Button ingredientsButton;

    @FXML
    private Button additionButton;

    @FXML
    private Button subtractionButton;

    @FXML
    private Button refreshButton;

    /**
     * Constructs a new AppViewCtrl with the necessary dependencies.
     *
     * @param server   the server utility used for network communication
     * @param mainCtrl the main controller used for scene navigation
     */
    @Inject
    public AppViewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        itemsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Showable item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });

        itemsList.getSelectionModel().selectedItemProperty().addListener((
                obs, oldVal, newVal) -> {
            if (newVal != null && newVal instanceof Recipe) {
                mainCtrl.showRecipe((Recipe) newVal);
            }
        });
        loadRecipes();
    }

    /**
     * Sets the content displayed in the content root area.
     *
     * @param content the parent node to display
     */
    public void setContent(Parent content) {
        contentRoot.getChildren().clear();
        contentRoot.getChildren().add(content);
    }

    /**
     * Fetches the complete list of recipes from the server and updates the recipe ListView.
     * <p>
     * This method runs asynchronously to avoid blocking the UI thread. If the server
     * is unreachable, an error alert is displayed to the user.
     */
    public void loadRecipes() {
        try {
            // Fetch from server
            List<Recipe> recipes = server.getRecipes();

            // Update UI on JavaFX Application Thread
            Platform.runLater(() -> {
                itemsList.setItems(FXCollections.observableArrayList(recipes));
            });
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText("Could not load recipes");
                alert.setContentText("Check if the server is running.");
                alert.showAndWait();
            });
        }
    }

    /**
     * gne
     */
    public void addRecipes(){
        String name = "food";
        int servings = 2;
        List<String> preparationList = List.of("hello", "teo");
        Recipe recipe = new Recipe(name, servings, preparationList);
        server.add(recipe);
    }
}
