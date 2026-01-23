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
import commons.RecipeIngredient;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * The Main Controller that manages the execution flow and scene switching.
 */
public class MainCtrl {

    private Stage primaryStage;
    private Stage ingredientAddStage;
    private Stage shoppingListStage;
    private Stage shoppingListConfirmationStage;

    private AppViewCtrl appViewCtrl;
    private AddRecipeCtrl addRecipeCtrl;
    private ShoppingListCtrl shoppingListCtrl;
    private ShoppingListConfirmationCtrl shoppingListConfirmationCtrl;

    private MyFXML fxml;
    private boolean firstOpen;
    private FavoritesPollingService pollingService;

    private Locale locale = Locale.ENGLISH;
    private final Preferences prefs = Preferences.userNodeForPackage(MainCtrl.class);
    private String flagPath = "/images/UK-flag.png";

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

        locale = Locale.forLanguageTag(prefs.get("lang", "en"));
        flagPath = prefs.get("flagPath", "/images/UK-flag.png");

        primaryStage.setResizable(false);

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
        var overview = fxml.load(AppViewCtrl.class, bundle(),
                "client", "scenes", "AppView.fxml");

        this.appViewCtrl = overview.getKey();
        primaryStage.setScene(new Scene(overview.getValue()));

        appViewCtrl.applyLanguageIcon(flagPath);
    }

    /**
     * Opens the recipe view for the selected recipe in the AppView content area.
     *
     * @param recipe the recipe to display
     */
    public void showRecipe(Recipe recipe) {
        // Removed obsolete temp file cleanup code here

        if (fxml == null || appViewCtrl == null) {
            throw new IllegalStateException("FXML or AppViewCtrl are null");
        }
        Pair<RecipeViewCtrl, Parent> recipeView = fxml.load(RecipeViewCtrl.class, bundle(),
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
        // Removed obsolete temp file cleanup code here

        if (fxml == null || appViewCtrl == null) {
            throw new IllegalStateException("FXML or AppViewCtrl are null");
        }
        Pair<IngredientViewCtrl, Parent> ingredientView = fxml.load(IngredientViewCtrl.class, bundle(),
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
        Pair<AddIngredientCtrl, Parent> addIngredientView = fxml.load(AddIngredientCtrl.class, bundle(),
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
        Pair<AddRecipeCtrl, Parent> addRecipeView = fxml.load(AddRecipeCtrl.class, bundle(),
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
        Pair<RecipeViewCtrl, Parent> defaultScreen = fxml.load(RecipeViewCtrl.class, bundle(),
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
                    fxml.load(ShoppingListCtrl.class, bundle(),
                    "client", "scenes", "ShoppingList.fxml");
            shoppingListStage = new Stage();
            shoppingListStage.setTitle("Shopping List");
            shoppingListStage.setScene(new Scene(shoppingListView.getValue()));
            shoppingListStage.setResizable(false);
            shoppingListCtrl = shoppingListView.getKey();

            shoppingListCtrl.initialize(fxml, getBundle());
        }
        shoppingListStage.show();
        shoppingListStage.toFront();
        reloadShoppingList();
    }

    /**
     * called when adding new elements to the shopping list to reload it
     */
    public void reloadShoppingList(){
        if (shoppingListCtrl != null) {
            shoppingListCtrl.loadShoppingList();
        }
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
     * Resets the app to the default view and applies language change
     * @param locale path to the properties file
     * @param flagPath path to the flag's image
     */
    public void changeLanguageAndReset(Locale locale, String flagPath) {
        setLocale(locale);
        setFlagPath(flagPath);
        showAppView();
        showDefaultView();
        //herea
    }

    /**
     * Provides file with encoded labels
     * @return ResourceBundle file with hardcoded text in chosen language
     */
    private ResourceBundle bundle() {
        return ResourceBundle.getBundle("i18n.messages", locale);
    }

    /**
     * Provides file with encoded labels for other Controllers
     * @return ResourceBundle file with hardcoded text in chosen language
     */
    public ResourceBundle getBundle() {
        return ResourceBundle.getBundle("i18n.messages", locale);
    }

    /**
     * Setter method for updating path to the Properties file
     * @param locale Locale with path to the file
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        prefs.put("lang", locale.toLanguageTag());
    }

    /**
     * Getter for the Locale variable
     * @return Locale for chosen language
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Setter for path to a flag's image
     * @param flagPath path to the chosen language flag's image
     */
    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
        prefs.put("flagPath", flagPath);
    }

    /**
     * Getter for the flag path
     * @return String of the path to the image
     */
    public String getFlagPath() {
        return flagPath;
    }

    /**
     * To show an error for if something goes wrong
     * @param header The head text of the error
     * @param content The main text of the error
     */
    public void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Opens a new window with addIngredients view.
     */
    public Ingredient showAddIngredientsNewWindow() {
        ingredientAddStage = new Stage();
        Pair<AddIngredientCtrl, Parent> addIngredientView = fxml.load(AddIngredientCtrl.class, bundle(),
                "client", "scenes", "AddIngredient.fxml");
        AddIngredientCtrl addIngredientCtrl = addIngredientView.getKey();
        addIngredientCtrl.initialize(true);
        ingredientAddStage.setScene(new Scene(addIngredientView.getValue()));
        ingredientAddStage.initModality(Modality.APPLICATION_MODAL);
        ingredientAddStage.initOwner(primaryStage);
        ingredientAddStage.setTitle("Add Ingredient");
        ingredientAddStage.setResizable(false);
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

    /**
     * opens the shopping list confirmation stage for the given ingredient list
     * @param ingredients the list of ingredients will be shown
     * @param scalar a scaling value for the ingredients
     * @param recipeName the name of the recipe
     */
    public void openShoppingListConfirmation(List<RecipeIngredient> ingredients, double scalar, String recipeName) {
        if (shoppingListConfirmationCtrl == null || shoppingListConfirmationStage == null){
            Pair<ShoppingListConfirmationCtrl, Parent> shoppingListConfirmation = fxml.load(ShoppingListConfirmationCtrl.class, bundle(),
                    "client", "scenes", "ShoppingListConfirmation.fxml");
            shoppingListConfirmationStage = new Stage();
            shoppingListConfirmationStage.setTitle("Shopping List Confirmation");
            shoppingListConfirmationStage.setScene(new Scene(shoppingListConfirmation.getValue()));
            shoppingListConfirmationStage.setResizable(false);
            shoppingListConfirmationCtrl = shoppingListConfirmation.getKey();

            shoppingListConfirmationCtrl.initialize(fxml, shoppingListConfirmationStage);
        }
        shoppingListConfirmationStage.show();
        shoppingListConfirmationStage.toFront();
        shoppingListConfirmationCtrl.loadList(ingredients, scalar, recipeName);
    }
}