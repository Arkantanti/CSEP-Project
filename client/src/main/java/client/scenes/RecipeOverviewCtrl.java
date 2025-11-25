package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RecipeOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private ListView<Recipe> recipeList;

    @FXML
    private TextField recipeTitle;

    @FXML
    private ListView<RecipeIngredient> ingredientList;

    @FXML
    private ListView<String> preparationStepList;


    /**
     * Constructs a new RecipeOverviewCtrl
     * @param server   the server utility for network operations
     * @param mainCtrl the main controller for scene navigation
     */
    @Inject
    public RecipeOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Recipe testRecipe = new Recipe("Recipe0", 2, new ArrayList<>());
        Recipe testRecipe2 = new Recipe("Recipe1", 2, new ArrayList<>());
        Recipe testRecipe3 = new Recipe("Recipe3", 2, new ArrayList<>());

        // example usage
        recipeList.getItems().addAll(testRecipe, testRecipe2, testRecipe3,
                new Recipe("Recipe4", 2, new ArrayList<>()),
                new Recipe("Recipe5", 2, new ArrayList<>()),
                new Recipe("Recipe6", 2, new ArrayList<>()),
                new Recipe("Recipe7", 2, new ArrayList<>()),
                new Recipe("Recipe8", 2, new ArrayList<>()),
                new Recipe("Recipe9", 2, new ArrayList<>()),
                new Recipe("Recipe10", 2, new ArrayList<>()),
                new Recipe("Recipe11", 2, new ArrayList<>()),
                new Recipe("Recipe12", 2, new ArrayList<>()),
                new Recipe("Recipe13", 2, new ArrayList<>()),
                new Recipe("Recipe14", 2, new ArrayList<>()),
                new Recipe("Recipe15", 2, new ArrayList<>()),
                new Recipe("Recipe16", 2, new ArrayList<>()),
                new Recipe("Recipe17", 2, new ArrayList<>()),
                new Recipe("Recipe18", 2, new ArrayList<>()),
                new Recipe("Recipe19", 2, new ArrayList<>()),
                new Recipe("Recipe20", 2, new ArrayList<>()),
                new Recipe("Recipe21", 2, new ArrayList<>()),
                new Recipe("Recipe22", 2, new ArrayList<>()),
                new Recipe("Recipe23", 2, new ArrayList<>()),
                new Recipe("Recipe24", 2, new ArrayList<>()),
                new Recipe("Recipe25", 2, new ArrayList<>()),
                new Recipe("Recipe26", 2, new ArrayList<>()),
                new Recipe("Recipe27", 2, new ArrayList<>())
        );

        // detecting clicks in the listview
        recipeList.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                openRecipe(newValue);
            }
        });

        // setting up the listview to inform it what to show (IE just the list name)
        recipeList.setCellFactory(list -> new ListCell<Recipe>() {
            @Override
            protected void updateItem(Recipe item, boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    setText(item.getName());
                }
            }
        });

        // simple implementation, will have to be expanded to be able to add new ingredients.
        ingredientList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(RecipeIngredient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else{
                    setText(item.getIngredient().getName());
                }
            }
        });
    }

    /**
     * Opens a recipe onto the typing area
     * @param recipe the recipe to open
     */
    private void openRecipe(Recipe recipe) {
        // TODO: Implement recipe opening
        System.out.println("Selected " + recipe.getName());
    }

    /**
     * creates a new recipe (both locally and on the server), and opens it for the user to edit
     */
    public void createNewRecipe() {
        // TODO: implement recipe adding
    }

    /**
     * fetches all recipes from the server and displays them in the listview
     * should probably also reopen the currently open recipe
     * Also used by the refresh button
     */
    public void loadRecipes() {
        // TODO: implement recipe loading
        // set title
        // load ingredients
        // load steps
    }
}
