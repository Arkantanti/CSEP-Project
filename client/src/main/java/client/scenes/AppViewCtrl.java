package client.scenes;

import client.services.IngredientService;
import client.services.RecipeService;
import client.utils.FavoritesManager;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import commons.Showable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AppViewCtrl implements Initializable {

    private final MainCtrl mainCtrl;
    private final FavoritesManager favoritesManager;
    private final RecipeService recipeService;
    private final IngredientService ingredientService;

    // Track which screen is currently active
    private enum ViewMode { RECIPES, FAVORITES, INGREDIENTS }
    private ViewMode currentMode = ViewMode.RECIPES;

    @FXML private TextField searchField;
    @FXML private StackPane contentRoot;
    @FXML private ListView<Showable> itemsList;
    @FXML private Button recipesButton;
    @FXML private Button ingredientsButton;
    @FXML private Button additionButton;
    @FXML private Button refreshButton;
    @FXML private Button favoritesButton;
    @FXML private HBox overListHBox;

    /**
     * Constructs a new AppViewCtrl with the necessary dependencies.
     *
     * @param mainCtrl          the main controller used for scene navigation and global state
     * @param favoritesManager  the utility for managing user favorites
     * @param recipeService     the service for fetching and searching recipes
     * @param ingredientService the service for fetching and searching ingredients
     */
    @Inject
    public AppViewCtrl(MainCtrl mainCtrl, FavoritesManager favoritesManager,
                       RecipeService recipeService, IngredientService ingredientService) {
        this.mainCtrl = mainCtrl;
        this.favoritesManager = favoritesManager;
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupListCellFactory();
        setupSearch();

        additionButton.setOnAction(e -> mainCtrl.showAddRecipe());
        refreshButton.setOnAction(e -> refreshData());

        recipesButton.setOnAction(e -> switchToMode(ViewMode.RECIPES));
        favoritesButton.setOnAction(e -> switchToMode(ViewMode.FAVORITES));
        ingredientsButton.setOnAction(e -> switchToMode(ViewMode.INGREDIENTS));

        // Initial load
        switchToMode(ViewMode.RECIPES);
    }

    /**
     * Sets up the custom cell factory to display items and handle clicks.
     */
    private void setupListCellFactory() {
        itemsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Showable item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String name = item.getName();
                    if (item instanceof Recipe && favoritesManager.isFavorite(((Recipe) item).getId())) {
                        name += " ★";
                    }
                    setText(name);
                }
            }
        });

        itemsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal instanceof Recipe) mainCtrl.showRecipe((Recipe) newVal);
            else if (newVal instanceof Ingredient) mainCtrl.showIngredient((Ingredient) newVal);
        });
    }

    /**
     * Configures the search field listener and ESC key behavior.
     */
    private void setupSearch() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshData());

            searchField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    searchField.clear();
                    contentRoot.requestFocus();
                    event.consume();
                }
            });
        }
    }

    /**
     * Switches the view mode and refreshes the data.
     */
    private void switchToMode(ViewMode mode) {
        this.currentMode = mode;

        // Show/Hide recipe-specific buttons
        boolean showRecipeControls = (mode != ViewMode.INGREDIENTS);
        overListHBox.setVisible(showRecipeControls);
        overListHBox.setManaged(showRecipeControls);

        refreshData();
    }

    /**
     * Refreshes the list based on the current mode and search query.
     */
    public void refreshData() {
        String query = (searchField != null) ? searchField.getText() : "";
        boolean isSearch = (query != null && !query.isBlank());

        try {
            List<? extends Showable> items;

            // Delegate logic to services based on current ViewMode
            switch (currentMode) {
                case FAVORITES:
                    items = isSearch ? recipeService.searchFavoriteRecipes(query)
                            : recipeService.getFavoriteRecipes();
                    break;
                case INGREDIENTS:
                    items = isSearch ? ingredientService.searchIngredients(query)
                            : ingredientService.getAllIngredients();
                    break;
                case RECIPES:
                default:
                    items = isSearch ? recipeService.searchRecipes(query)
                            : recipeService.getAllRecipes();
                    break;
            }

            Platform.runLater(() -> itemsList.setItems(FXCollections.observableArrayList(items)));

        } catch (Exception e) {
            e.printStackTrace();
            showError("Connection Error", "Could not load data. Check if server is running.");
        }
    }

    // --- Public methods required by other controllers ---

    public void loadRecipes() { switchToMode(ViewMode.RECIPES); }
    public void loadFavorites() { switchToMode(ViewMode.FAVORITES); }
    public void loadIngredients() { switchToMode(ViewMode.INGREDIENTS); }

    /**
     * Replaces the content in the center stack pane.
     *
     * @param content the new Parent node to display
     */
    public void setContent(Parent content) {
        contentRoot.getChildren().clear();
        contentRoot.getChildren().add(content);
    }

    /**
     * Opens the shopping list scene via the main controller.
     */
    @FXML
    private void openShoppingList() {
        mainCtrl.openShoppingList();
    }

    /**
     * Helper method to display an error alert to the user.
     *
     * @param title   the title of the alert
     * @param content the content message of the alert
     */
    private void showError(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}