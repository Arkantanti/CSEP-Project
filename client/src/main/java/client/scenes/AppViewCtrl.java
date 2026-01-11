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

    // Enum to track the current view state
    private enum ViewMode {
        RECIPES,
        FAVORITES,
        INGREDIENTS
    }

    private ViewMode currentMode = ViewMode.RECIPES;

    @FXML private TextField searchField;
    @FXML private StackPane contentRoot;
    @FXML private ListView<Showable> itemsList;
    @FXML private Button recipesButton;
    @FXML private Button ingredientsButton;
    @FXML private Button additionButton;
    @FXML private Button subtractionButton;
    @FXML private Button refreshButton;
    @FXML private Button favoritesButton;
    @FXML private HBox overListHBox;

    /**
     * Constructs a new AppViewCtrl with the necessary dependencies.
     *
     * @param mainCtrl          The main controller for scene navigation.
     * @param favoritesManager  The manager for handling user favorites.
     * @param recipeService     The service for fetching and searching recipes.
     * @param ingredientService The service for fetching and searching ingredients.
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

        // Initial load default
        switchToMode(ViewMode.RECIPES);
    }

    /**
     * Configures the custom CellFactory for the items ListView.
     * <p>
     * This handles the display logic for items in the list, such as appending
     * a star symbol ("★") to the names of favorite recipes. It also sets up
     * the selection listener to open the detailed view when an item is clicked.
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
            if (newVal instanceof Recipe) {
                mainCtrl.showRecipe((Recipe) newVal);
            } else if (newVal instanceof Ingredient) {
                mainCtrl.showIngredient((Ingredient) newVal);
            }
        });
    }

    /**
     * Sets up the listeners for the search text field.
     * <p>
     * Updates the data list whenever the text changes and adds a key listener
     * to clear the search and remove focus when the ESC key is pressed.
     */
    private void setupSearch() {
        if (searchField != null) {
            // Use the new refreshData() method which handles Services and ViewModes
            searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshData());

            searchField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    searchField.clear();
                    contentRoot.requestFocus(); // Remove focus
                    event.consume();
                }
            });
        }
    }

    /**
     * Refreshes the current view.
     * <p>
     * This method is added to maintain compatibility with the incoming changes (Theirs),
     * but it delegates to the new {@link #refreshData()} method to use the Service layer.
     */
    public void refresh() {
        refreshData();
    }

    /**
     * Switches the current view mode and updates the UI accordingly.
     *
     * @param mode The new {@link ViewMode} to switch to.
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
     * Refreshes the data displayed in the list based on the current mode and search query.
     * <p>
     * This method delegates the actual data fetching to {@link RecipeService} or
     * {@link IngredientService} and ensures the UI is updated on the JavaFX Application Thread.
     */
    public void refreshData() {
        String query = (searchField != null) ? searchField.getText() : "";
        boolean isSearch = (query != null && !query.isBlank());

        try {
            List<? extends Showable> items;

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

    // --- Public Adapter Methods (to keep compatibility with other controllers) ---

    /**
     * Switches the view to display all recipes.
     */
    public void loadRecipes() {
        switchToMode(ViewMode.RECIPES);
    }

    /**
     * Switches the view to display the user's favorite recipes.
     */
    public void loadFavorites() {
        switchToMode(ViewMode.FAVORITES);
    }

    /**
     * Switches the view to display all ingredients.
     */
    public void loadIngredients() {
        switchToMode(ViewMode.INGREDIENTS);
    }

    /**
     * Replaces the content in the center stack pane with the provided parent node.
     *
     * @param content The new content to display.
     */
    public void setContent(Parent content) {
        contentRoot.getChildren().clear();
        contentRoot.getChildren().add(content);
    }

    /**
     * Opens the shopping list scene.
     */
    @FXML
    private void openShoppingList() {
        mainCtrl.openShoppingList();
    }

    /**
     * Helper method to display an error alert to the user.
     *
     * @param title   The title of the alert dialog.
     * @param content The content text of the alert dialog.
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