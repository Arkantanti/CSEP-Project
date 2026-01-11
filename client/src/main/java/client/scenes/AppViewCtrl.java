package client.scenes;

import client.utils.FavoritesManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.input.KeyCode;
import java.util.stream.Collectors;


import java.net.URL;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AppViewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final FavoritesManager favoritesManager;
    private enum ViewMode {
        RECIPES,
        FAVORITES,
        INGREDIENTS
    }

    private ViewMode currentView = ViewMode.RECIPES;

    @FXML
    private javafx.scene.control.TextField searchField;
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

    @FXML
    private Button favoritesButton;

    @FXML
    private HBox overListHBox;


    /**
     * Constructs a new AppViewCtrl with the necessary dependencies.
     *
     * @param server   the server utility used for network communication
     * @param mainCtrl the main controller used for scene navigation
     */
    @Inject
    public AppViewCtrl(ServerUtils server, MainCtrl mainCtrl, FavoritesManager favoritesManager) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.favoritesManager = favoritesManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        itemsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Showable item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String name = item.getName();
                    if (item instanceof Recipe) {
                        Recipe recipe = (Recipe) item;
                        if (favoritesManager.isFavorite(recipe.getId())) {
                            name = name + " ★";
                        }
                    }
                    setText(name);
                }
            }
        });

        itemsList.getSelectionModel().selectedItemProperty().addListener((
                obs, oldVal, newVal) -> {
            if (newVal instanceof Recipe) {
                mainCtrl.showRecipe((Recipe) newVal);
            } else if (newVal instanceof Ingredient) {
                mainCtrl.showIngredient((Ingredient) newVal);
            }

        });

        additionButton.setOnAction(e -> mainCtrl.showAddRecipe());

        recipesButton.setOnAction(e -> loadRecipes());

        favoritesButton.setOnAction(e -> loadFavorites());

        if (searchField != null) {
            searchField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    searchField.clear();
                    itemsList.requestFocus();
                }
            });
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isBlank()) {
                    if (currentView == ViewMode.FAVORITES) {
                        loadFavorites();
                    } else {
                        loadRecipes();
                    }
                } else {
                    if (currentView == ViewMode.FAVORITES) {
                        searchFavorites(newValue);
                    } else {
                        searchRecipes(newValue);
                    }
                }
            });
        }
        loadRecipes();
    }

    /**
     * Runs when the refresh button is clicked. Either refreshes the ingredients or refreshes the
     * recipes.
     */
    public void refresh() {
        switch(currentView) {
            case RECIPES: loadRecipes(); break;
            case FAVORITES: loadFavorites(); break;
            case INGREDIENTS: loadIngredients(); break;
        }
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
        currentView = ViewMode.RECIPES;
        overListHBox.setVisible(true);
        overListHBox.setManaged(true);
        try {
            // Fetch from server
            List<Recipe> recipes = server.getRecipes();
            recipes.sort(Comparator.comparing(Recipe::getName));

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
     * Searches for recipes via the server and updates the UI list.
     * @param query The text to search for
     */
    private void searchRecipes(String query) {
        try {
            List<Recipe> results = server.searchRecipes(query);
            results.sort(Comparator.comparing(Recipe::getName));
            // JavaFX UI updates must run on the UI thread
            Platform.runLater(() -> {
                itemsList.setItems(FXCollections.observableArrayList(results));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the shopping list window
     */
    @FXML
    private void openShoppingList(){
        mainCtrl.openShoppingList();
    }

    /**
     * Fetches the complete list of ingredients from the server and updates the recipe ListView.
     * <p>
     * This method runs asynchronously to avoid blocking the UI thread. If the server
     * is unreachable, an error alert is displayed to the user.
     */
    public void loadIngredients() {
        currentView = ViewMode.INGREDIENTS;
        overListHBox.setVisible(false);
        overListHBox.setManaged(false);
        try {
            // Fetch from server
            List<Ingredient> ingredients = server.getIngredients();
            ingredients.sort(Comparator.comparing(Ingredient::getName));

            // Update UI on JavaFX Application Thread
            Platform.runLater(() -> {
                itemsList.setItems(FXCollections.observableArrayList(ingredients));

            });
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText("Could not load ingredients");
                alert.setContentText("Check if the server is running.");
                alert.showAndWait();
            });
        }
    }
    /**
     * Loads list of favorite recipes and displays in the list view.
     */
    public void loadFavorites() {
        currentView = ViewMode.FAVORITES;
        try {
            List<Recipe> allRecipes = server.getRecipes();
            List<Recipe> favoriteRecipes = new ArrayList<>();

            for (Recipe recipe : allRecipes) {
                if (favoritesManager.isFavorite(recipe.getId())) {
                    favoriteRecipes.add(recipe);
                }
            }

            Platform.runLater(() -> {
                itemsList.setItems(FXCollections.observableArrayList(favoriteRecipes));
            });
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText("Could not load favorite recipes");
                alert.setContentText("Check if the server is running.");
                alert.showAndWait();
            });
        }
    }

    /**
     * Searches for recipes matching the query, then filters for favorites.
     * @param query The text to search for
     */
    private void searchFavorites(String query) {
        try {
            // 1. Get matches from server (or all recipes if server search isn't preferred)
            List<Recipe> searchResults = server.searchRecipes(query);

            // 2. Filter these results to keep only favorites
            List<Recipe> favoriteResults = searchResults.stream()
                    .filter(r -> favoritesManager.isFavorite(r.getId()))
                    .sorted(Comparator.comparing(Recipe::getName))
                    .collect(Collectors.toList());

            // 3. Update UI
            Platform.runLater(() -> {
                itemsList.setItems(FXCollections.observableArrayList(favoriteResults));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
