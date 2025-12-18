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
import commons.Recipe;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.nio.file.Path;

/**
 * The Main Controller that manages the execution flow and scene switching.
 */
public class MainCtrl {

    private Stage primaryStage;

    private AppViewCtrl appViewCtrl;
    private Scene appView;
    private MyFXML fxml;
    private AddRecipeCtrl addRecipeCtrl;
    private boolean firstOpen;

    /**
     * Initializes the main controller with the primary stage and the necessary scenes.
     *
     * @param primaryStage the primary stage of the application
     * @param appView the pair containing the controller and parent for the overview scene
     * @param fxml the FXML loader for loading views
     */
    public void initialize(Stage primaryStage, Pair<AppViewCtrl, Parent> appView, MyFXML fxml) {
        this.primaryStage = primaryStage;
        this.appViewCtrl = appView.getKey();
        this.appView = new Scene(appView.getValue());
        this.fxml = fxml;
        this.firstOpen = true;

        appViewCtrl.setContent(new javafx.scene.control.Label("Select a recipe from the list"));

        showAppView();
        primaryStage.show();
        showDefaultScreen();
    }

    /**
     * Displays the recipe overview scene.
     * Sets the title and refreshes the data in the overview controller.
     */
    public void showAppView() {
        primaryStage.setTitle("FoodPal");
        primaryStage.setScene(appView);
    }

    /**
     * Opens the recipe view for the selected recipe in the AppView content area.
     *
     * @param recipe the recipe to display
     */
    public void showRecipe(Recipe recipe) {
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
    }

    /**
     * The function to show the addRecipe fxml file
     */
    public void showAddRecipe() {
        Pair<AddRecipeCtrl, Parent> addRecipeView = fxml.load(AddRecipeCtrl.class,
                "client", "scenes", "AddRecipe.fxml");
        this.addRecipeCtrl = addRecipeView.getKey();
        appViewCtrl.setContent(addRecipeView.getValue());
    }

    /**
     * function to make sure there are no problems when the app is first opened
     * and then a recipe gets added and canceled immediately.
     */
    public void showDefaultScreen(){
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
        chooser.setTitle("Choose Markdown Save Location");
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
        Pair<ShoppingListCtrl, Parent> shoppingListView = fxml.load(ShoppingListCtrl.class, "client", "scenes", "ShoppingList.fxml");
        Stage stage = new Stage();
        stage.setTitle("Shopping List");
        stage.setScene(new Scene(shoppingListView.getValue()));
        stage.show();

        shoppingListView.getKey().initialize();
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

    public AddRecipeCtrl getAddRecipeCtrl() {
        return addRecipeCtrl;
    }

    /**
     * function to get the appViewCtrl
     * @return the appViewCtrl
     */
    public AppViewCtrl getAppViewCtrl(){
        return appViewCtrl;
    }
}