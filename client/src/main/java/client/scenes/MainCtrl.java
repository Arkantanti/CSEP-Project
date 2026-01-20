/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.MyFXML;
import client.services.WebsocketService;
import client.utils.FavoritesManager;
import client.utils.FavoritesPollingService;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.nio.file.Path;

/**
 * The Main Controller that manages the execution flow and scene switching.
 */
public class MainCtrl {

    private Stage primaryStage;
    private Stage ingredientAddStage;
    private Stage shoppingListStage;

    private AppViewCtrl appViewCtrl;
    private AddRecipeCtrl addRecipeCtrl;
    private ShoppingListCtrl shoppingListCtrl;

    private MyFXML fxml;
    private boolean firstOpen;
    private FavoritesPollingService pollingService;

    private WebsocketService websocketService;

    /**
     * Injected Constructor
     * @param websocketService the websocket service
     */
    @Inject
    public MainCtrl(WebsocketService websocketService){
        this.websocketService = websocketService;
    }

    /**
     * Initializes the main controller with the primary stage and the necessary scenes.
     *
     * @param primaryStage the primary stage of the application
     * @param fxml the FXML loader for loading views
     * @param favoritesManager the manager for handling favorites
     */
    public void initialize(Stage primaryStage, MyFXML fxml, FavoritesManager favoritesManager) {


        this.primaryStage = primaryStage;
        this.fxml = fxml;
        this.firstOpen = true;

        showAppView();
        primaryStage.show();
        showDefaultView();

        pollingService = new FavoritesPollingService(favoritesManager);
        pollingService.setMainCtrl(this);
        pollingService.startPollingService();

        websocketService.initialize(appViewCtrl, this);
    }

    /**
     * Displays the recipe overview scene.
     * Sets the title and refreshes the data in the overview controller.
     */
    public void showAppView() {
        primaryStage.setTitle("FoodPal");
        var overview = fxml.load(AppViewCtrl.class,
                "client", "scenes", "AppView.fxml");

        this.appViewCtrl = overview.getKey();
        primaryStage.setScene(new Scene(overview.getValue()));
    }

    /**
     * Opens the recipe view for the selected recipe in the AppView content area.
     *
     * @param recipe the recipe to display
     */
    public void showRecipe(Recipe recipe) {
        if (    (addRecipeCtrl != null) &&
                !addRecipeCtrl.getIsSaved() &&
                (addRecipeCtrl.getRecipe() != null)) {
            addRecipeCtrl.deleter(addRecipeCtrl.getRecipe().getId());
            addRecipeCtrl.setIsSavedTrue();
        }
        if (fxml == null || appViewCtrl == null) {
            throw new IllegalStateException("FXML or AppViewCtrl are null");
        }
        Pair<RecipeViewCtrl, Parent> recipeView = fxml.load(RecipeViewCtrl.class,
                "client", "scenes", "RecipeView.fxml");
        recipeView.getKey().setRecipe(recipe, fxml);
        appViewCtrl.setContent(recipeView.getValue());
        if(firstOpen){
            switchFirstOpen();
        }

        websocketService.setRecipeViewCtrl(recipeView.getKey());
    }

    /**
     * Opens the ingredient view for the selected recipe in the AppView content area.
     *
     * @param ingredient the recipe to display
     */
    public void showIngredient(Ingredient ingredient) {
        if (    (addRecipeCtrl != null) &&
                !addRecipeCtrl.getIsSaved() &&
                (addRecipeCtrl.getRecipe() != null)) {
            addRecipeCtrl.deleter(addRecipeCtrl.getRecipe().getId());
            addRecipeCtrl.setIsSavedTrue();
        }
        if (fxml == null || appViewCtrl == null) {
            throw new IllegalStateException("FXML or AppViewCtrl are null");
        }
        Pair<IngredientViewCtrl, Parent> ingredientView = fxml.load(IngredientViewCtrl.class,
                "client", "scenes", "IngredientView.fxml");
        ingredientView.getKey().setIngredient(ingredient, fxml);
        appViewCtrl.setContent(ingredientView.getValue());
        if(firstOpen){
            switchFirstOpen();
        }
    }

    /**
     * Sets the content to the addIngredient.fxml.
     */

    public void showAddIngredient() {
        Pair<AddIngredientCtrl, Parent> addIngredientView = fxml.load(AddIngredientCtrl.class,
                "client", "scenes", "AddIngredient.fxml");
        addIngredientView.getKey().initialize(false);
        appViewCtrl.setContent(addIngredientView.getValue());
    }

    /**
     * The function to show the addRecipe fxml file
     */
    public void showAddRecipe() {
        if (    (addRecipeCtrl != null) &&
                !addRecipeCtrl.getIsSaved() &&
                (addRecipeCtrl.getRecipe() != null)) {
            addRecipeCtrl.deleter(addRecipeCtrl.getRecipe().getId());
            addRecipeCtrl.setIsSavedTrue();
        }
        Pair<AddRecipeCtrl, Parent> addRecipeView = fxml.load(AddRecipeCtrl.class,
                "client", "scenes", "AddRecipe.fxml");
        this.addRecipeCtrl = addRecipeView.getKey();
        addRecipeCtrl.initialize(fxml);
        appViewCtrl.setContent(addRecipeView.getValue());
    }

    /**
     * Function to make sure there are no problems when the app is first opened
     * and then a recipe gets added and canceled immediately.
     */
    public void showDefaultView(){
        Pair<RecipeViewCtrl, Parent> defaultScreen = fxml.load(RecipeViewCtrl.class,
                "client", "scenes", "DefaultView.fxml");
        appViewCtrl.setContent(defaultScreen.getValue());
    }

    /**
     * Opens the FileChooser windows and returns the path of the file chosen by the user.
     * The user is forced to choose a Markdown file.
     * @return {@link Path} chosen by the user
     */
    public Path showFileChooser(String placeholder) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose PDF Save Location");
        chooser.setInitialFileName(placeholder);

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        File file = chooser.showSaveDialog(primaryStage);

        if (file != null) {
            return file.toPath();
        }

        return null;
    }

    /**
     * Opens the shopping list window
     */
    public void openShoppingList(){
        if (shoppingListStage == null || shoppingListCtrl == null) {
            Pair<ShoppingListCtrl, Parent> shoppingListView =
                    fxml.load(ShoppingListCtrl.class, "client", "scenes", "ShoppingList.fxml");
            shoppingListStage = new Stage();
            shoppingListStage.setTitle("Shopping List");
            shoppingListStage.setScene(new Scene(shoppingListView.getValue()));
            shoppingListCtrl = shoppingListView.getKey();

            shoppingListCtrl.initialize(fxml);
        }
        shoppingListStage.show();
        shoppingListStage.toFront();
        reloadShoppingList();
    }

    /**
     * called when adding new elements to the shopping list to reload it
     */
    public void reloadShoppingList(){
        shoppingListCtrl.loadShoppingList();
    }

    /**
     * The switch to change if it was opened or not.
     */
    public void switchFirstOpen(){
        this.firstOpen = !this.firstOpen;
    }

    public boolean getFirstOpen(){
        return this.firstOpen;
    }

    /**
     * function to get the appViewCtrl
     * @return the appViewCtrl
     */
    public AppViewCtrl getAppViewCtrl(){
        return appViewCtrl;
    }

    public AddRecipeCtrl getAddRecipeCtrl() {return addRecipeCtrl; }

    /**
     * Method to load recipes from the main controller so that
     * the main controller acts as the main orchestrator.
     * This method is also used by the PollingService to reload recipes through the main controller.
     */
    public void reloadRecipes() {
        if (appViewCtrl != null) {
            appViewCtrl.loadRecipes();
        } else {
            System.out.println("Tried to reload recipes from the main controller, " +
                    "but app view controller was not initialized.");
        }
    }

    /**
     * Shuts down the polling service when the application closes.
     */
    public void shutdown() {
        if (pollingService != null) {
            pollingService.shutdown();
        } else {
            System.out.println("Tried to shutdown the polling service but it was not initialized.");
        }
    }

    /**
     * Opens a new window with addIngredients view.
     */
    public Ingredient showAddIngredientsNewWindow() {
        ingredientAddStage = new Stage();
        Pair<AddIngredientCtrl, Parent> addIngredientView = fxml.load(AddIngredientCtrl.class,
                "client", "scenes", "AddIngredient.fxml");
        AddIngredientCtrl addIngredientCtrl = addIngredientView.getKey();
        addIngredientCtrl.initialize(true);
        ingredientAddStage.setScene(new Scene(addIngredientView.getValue()));
        ingredientAddStage.initModality(Modality.APPLICATION_MODAL);
        ingredientAddStage.initOwner(primaryStage);
        ingredientAddStage.setTitle("Add Ingredient");
        ingredientAddStage.showAndWait();

        if(addIngredientCtrl.getIngredientSaved()) {
            return addIngredientCtrl.getIngredient();
        } else {
            return null;
        }
    }


    /**
     * Closes the secondary stage.
     */
    public void closeAddIngredientWindow() {
        if(ingredientAddStage != null) {
            ingredientAddStage.close();
        }
    }
}