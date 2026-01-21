package client.scenes;

import client.services.IngredientService;
import client.services.RecipeService;
import client.utils.FavoritesManager;
import client.utils.PreferenceManager;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the main application view.
 * <p>
 * This controller acts as the central hub for the main scene. It manages:
 * <ul>
 * <li>The sidebar navigation between Recipes, Favorites, and Ingredients.</li>
 * <li>The list of items displayed to the user.</li>
 * <li>Search functionality (delegated to services).</li>
 * <li>Switching the content area based on user interaction.</li>
 * </ul>
 */
public class AppViewCtrl implements Initializable {

    private final MainCtrl mainCtrl;
    private final FavoritesManager favoritesManager;
    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final PreferenceManager preferenceManager;

    /**
     * Enum to track the currently active view mode.
     * This determines which data source is used (Recipes service vs Ingredients service)
     * and how the search functionality behaves.
     */
    private enum ViewMode {
        /** Display all recipes. */
        RECIPES,
        /** Display only favorite recipes. */
        FAVORITES,
        /** Display all ingredients. */
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
    @FXML private MenuButton languageMenu;
    @FXML private ImageView languageIcon;
    @FXML private CheckBox englishCheck;
    @FXML private CheckBox polishCheck;
    @FXML private CheckBox dutchCheck;

    private boolean engLanguage;
    private boolean polLanguage;
    private boolean dutLanguage;

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
                       RecipeService recipeService, IngredientService ingredientService,
                       PreferenceManager preferenceManager) {
        this.mainCtrl = mainCtrl;
        this.favoritesManager = favoritesManager;
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.preferenceManager = preferenceManager;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. It sets up the list cell factory,
     * search listeners, and button actions.
     *
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources used to localize the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadLanguagePreferences();

        setupListCellFactory();
        setupSearch();

        // Button Actions (Preserving logic from "Theirs")
        additionButton.setOnAction(e -> mainCtrl.showAddRecipe());
        refreshButton.setOnAction(e -> refreshData()); // Calls the public refresh method

        recipesButton.setOnAction(e -> switchToMode(ViewMode.RECIPES));
        favoritesButton.setOnAction(e -> switchToMode(ViewMode.FAVORITES));
        ingredientsButton.setOnAction(e -> switchToMode(ViewMode.INGREDIENTS));

        // Set default view to Recipes
        switchToMode(ViewMode.RECIPES);
    }

    /**
     * Load language preferences from saved config
     */
    private void loadLanguagePreferences() {
        try {
            engLanguage = preferenceManager.isEnglishEnabled();
            polLanguage = preferenceManager.isPolishEnabled();
            dutLanguage = preferenceManager.isDutchEnabled();

            // Sync checkboxes with loaded values
            englishCheck.setSelected(engLanguage);
            polishCheck.setSelected(polLanguage);
            dutchCheck.setSelected(dutLanguage);
        } catch (Exception e) {
            // Use defaults if loading fails
            engLanguage = true;
            polLanguage = true;
            dutLanguage = true;

            englishCheck.setSelected(true);
            polishCheck.setSelected(true);
            dutchCheck.setSelected(true);
        }
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
                    // Add visual indicator for favorites
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
     * Switches the current view mode and updates the UI accordingly.
     * Sets the content displayed in the content root area.
     *
     * @param mode The new {@link ViewMode} to switch to.
     */
    private void switchToMode(ViewMode mode) {
        this.currentMode = mode;

        // Show recipe-specific controls (like "Add Recipe") only when not in Ingredient mode
        boolean showRecipeControls = (mode != ViewMode.INGREDIENTS);
        overListHBox.setVisible(showRecipeControls);
        overListHBox.setManaged(showRecipeControls);

        refreshData();
        englishCheck.setSelected(engLanguage);
    }

    /**
     * Refreshes the data displayed in the list based on the current mode and search query.
     * <p>
     * This method delegates the actual data fetching to {@link RecipeService} or
     * {@link IngredientService} and ensures the UI is updated on the JavaFX Application Thread.
     */
    public void refreshData() {
        itemsList.getSelectionModel().clearSelection();
        String query = (searchField != null) ? searchField.getText() : "";
        boolean isSearch = (query != null && !query.isBlank());

        try {
            List<? extends Showable> items;

            switch (currentMode) {
                case FAVORITES:
                    items = isSearch ? recipeService.searchFavoriteRecipes(query)
                            : recipeService.getFavoriteRecipes();
                    additionButton.setOnAction(e -> mainCtrl.showAddRecipe());
                    break;
                case INGREDIENTS:
                    items = isSearch ? ingredientService.searchIngredients(query)
                            : ingredientService.getAllIngredients();
                    additionButton.setOnAction(e -> mainCtrl.showAddIngredient());
                    break;
                case RECIPES:
                default:
                    items = isSearch ? recipeService.searchRecipes(query)
                            : recipeService.getAllRecipesWithLanguage(engLanguage, polLanguage, dutLanguage);
                    additionButton.setOnAction(e -> mainCtrl.showAddRecipe());
                    break;
            }

            Platform.runLater(() -> itemsList.setItems(FXCollections.observableArrayList(items)));

        } catch (Exception e) {
            e.printStackTrace();
            showError("Connection Error", "Could not load data. Check if server is running.");
        }
    }

    // --- Public Adapter Methods (Required by MainCtrl or other scenes) ---

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

    /**
     * Triggered when UK flag is clicked.
     */
    @FXML
    private void setLangEN() {
        mainCtrl.changeLanguageAndReset(Locale.ENGLISH, "/images/UK-flag.png");
    }

    /**
     * Triggered when NL flag is clicked.
     */
    @FXML
    private void setLangNL() {
        mainCtrl.changeLanguageAndReset(Locale.forLanguageTag("nl-NL"), "/images/DUTCH-flag.png");
    }

    /**
     * Triggered when PL flag is clicked.
     */
    @FXML
    private void setLangPL() {
        mainCtrl.changeLanguageAndReset(Locale.forLanguageTag("pl-PL"), "/images/POLAND-flag.png");
    }

    /**
     * The function to see the English recipes
     */
    @FXML
    public void languageChangeEng() {
        engLanguage = englishCheck.isSelected();
        try {
            preferenceManager.updateLanguagePreference("english", engLanguage);
        } catch (IOException e) {
            showError("Save Error", "Could not save English language preference");
        }
        loadRecipes();
    }

    /**
     * The function to see the Polish recipes
     */
    @FXML
    public void languageChangePol() {
        polLanguage = polishCheck.isSelected();
        try {
            preferenceManager.updateLanguagePreference("polish", polLanguage);
        } catch (IOException e) {
            showError("Save Error", "Could not save Polish language preference");
        }
        loadRecipes();
    }

    /**
     * The function to see the Dutch recipes
     */
    @FXML
    public void languageChangeDut() {
        dutLanguage = dutchCheck.isSelected();
        try {
            preferenceManager.updateLanguagePreference("dutch", dutLanguage);
        } catch (IOException e) {
            showError("Save Error", "Could not save Dutch language preference");
        }
        loadRecipes();
    }

    /**
     * Helper method for updatnig the flag icon on the language menu.
     * @param flagPath the path to the flag's image
     */
    public void applyLanguageIcon(String flagPath) {
        languageIcon.setImage(new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResourceAsStream(flagPath))));
    }

    public CheckBox getDutchCheck() {
        return dutchCheck;
    }

    public CheckBox getEnglishCheck() {
        return englishCheck;
    }

    public CheckBox getPolishCheck() {
        return polishCheck;
    }

    public boolean isDutLanguage() {
        return dutLanguage;
    }

    public boolean isEngLanguage() {
        return engLanguage;
    }

    public boolean isPolLanguage() {
        return polLanguage;
    }
}