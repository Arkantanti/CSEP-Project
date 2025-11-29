package client.scenes;

import client.utils.Printer;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class RecipeOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Printer printer;

    @FXML
    private ListView<Recipe> recipeList;

    @FXML
    private TextField recipeTitle;

    @FXML
    private ListView<RecipeIngredient> ingredientList;

    @FXML
    private ListView<String> preparationStepList;

    /**
     * Constructs a new RecipeOverviewCtrl with the necessary dependencies.
     *
     * @param server   the server utility used for network communication
     * @param mainCtrl the main controller used for scene navigation
     */
    @Inject
    public RecipeOverviewCtrl(ServerUtils server, MainCtrl mainCtrl, Printer printer) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.printer = printer;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Setup the List for Recipes (Show only name)
        recipeList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Recipe item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });

        // 2. Setup the Listener (When clicked, open details)
        recipeList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                openRecipe(newVal);
            }
        });

        // 3. Setup the List for Ingredients (Show name and amount)
        ingredientList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(RecipeIngredient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Formats as: "Potato (500 GRAM)"
                    String text = item.getIngredient().getName();
                    if (item.getAmount() > 0) {
                        text += " (" + item.getAmount() + " "
                                + (item.getUnit() != null ? item.getUnit() : "") + ")";
                    }
                    setText(text);
                }
            }
        });

        // 4. Initial Load
        loadRecipes();
    }

    /**
     * Populates the detail view (Title, Ingredients, Steps) with data from the selected recipe.
     * Clears the list views if the recipe data is null.
     *
     * @param recipe the recipe to display
     */
    private void openRecipe(Recipe recipe) {
        // Update the UI fields with the selected recipe's data
        recipeTitle.setText(recipe.getName());

        // Populate Ingredients
        if (recipe.getIngredients() != null) {
            ingredientList.setItems(FXCollections.observableArrayList(recipe.getIngredients()));
        } else {
            ingredientList.getItems().clear();
        }

        // Populate Steps
        if (recipe.getPreparationSteps() != null) {
            preparationStepList.setItems(FXCollections
                    .observableArrayList(recipe.getPreparationSteps()));
        } else {
            preparationStepList.getItems().clear();
        }
    }

    /**
     * Handles the creation of a new recipe.
     * This method is triggered when the "Add" button is clicked.
     */
    public void createNewRecipe() {
        // TODO: Implement recipe adding logic
        System.out.println("Add button clicked");
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
                recipeList.setItems(FXCollections.observableArrayList(recipes));
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
     * Saves the currently selected recipe as a markdown file in a location specified by the user.
     */
    public void recipePrint() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();
        Path path = mainCtrl.showFileChooser("Recipe.pdf");
        if(recipe==null || path==null) {
            return;
        }
        try {
            String markdown = printer.recipePrint(recipe);
            printer.markdownToPDF(path, markdown);
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Saving error");
                alert.setHeaderText("Could not save the recipe");
                alert.showAndWait();
            });
        }
    }
}