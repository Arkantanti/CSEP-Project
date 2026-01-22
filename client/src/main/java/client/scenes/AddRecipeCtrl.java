package client.scenes;

import client.MyFXML;
import client.services.RecipeService;
import com.google.inject.Inject;
import client.utils.ServerUtils;
import commons.Language;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static commons.Recipe.recipeNameChecker;

public class AddRecipeCtrl {
    @FXML
    private Label nameLabel;
    @FXML
    private VBox ingredientsContainer;
    @FXML
    private Button ingredientAddButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<String> languageChoise;
    @FXML
    private CheckBox cheapCheckBox;
    @FXML
    private CheckBox fastCheckBox;
    @FXML
    private CheckBox veganCheckBox;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final RecipeService recipeService;

    private MyFXML fxml;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField servingsArea;
    @FXML
    private TextArea preparationsArea;

    private final AppViewCtrl appViewCtrl;

    private List<RecipeIngredient> recipeIngredientList;

    private Recipe recipe;
    private boolean isCloneMode = false;
    private boolean isSaved = false;

    /**
     *  The constructor for the add recipeController
     * @param server the server it is linked to
     */
    @Inject
    public AddRecipeCtrl(ServerUtils server, MainCtrl mainCtrl, RecipeService recipeService) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.appViewCtrl = mainCtrl.getAppViewCtrl();
        this.recipeService = recipeService;
    }

    /**
     * The stuff we need for that this begins so it can properly work.
     */
    public void initialize(MyFXML fxml){
        this.fxml = fxml;
        // Start with no recipe, as this will be created when needed.
        this.recipe = null;
        this.isCloneMode = false;
        this.isSaved = false;
    }

    /**
     * function for adding recipeIngredients
     */
    @FXML
    private void onAddRecipeIngredient(){
        // If no recipe exists yet, create it from current fields
        if (recipe == null) {
            String name = nameTextField.getText().trim();
            if (name.isEmpty()) {
                name = "New Recipe";
            }

            if(recipeNameChecker(recipeService.getAllRecipes(), name, this.recipe)){
                mainCtrl.showError("Name Used.", "This name is already in use.");
                return;
            }

            if(languageChoise == null){
                mainCtrl.showError("No language selected", "Choose a valid language");
                return;
            }

            int servings = 1;
            try {
                servings = Integer.parseInt(servingsArea.getText().trim());
            } catch (Exception e) {
                System.out.println("The value in servingsArea was invalid.");
            }

            List<String> steps = new ArrayList<>();
            if (!preparationsArea.getText().isEmpty()) {
                steps = Arrays.asList(preparationsArea.getText().split("\\r?\\n"));
            }
            boolean isCheap = cheapCheckBox.isSelected();
            boolean isFast = fastCheckBox.isSelected();
            boolean isVegan = veganCheckBox.isSelected();

            Language language = null;

            if(languageChoise.getValue().equals("English")){
                language = Language.English;
            } else if(languageChoise.getValue().equals("Dutch")){
                language = Language.Dutch;
            } else if(languageChoise.getValue().equals("Polish")){
                language = Language.Polish;
            }

            if (isCloneMode) {
                recipe = new Recipe(name, servings, steps, language, isCheap, isFast, isVegan);
            } else {
                recipe = server.add(new Recipe(name, servings, steps, language, isCheap, isFast, isVegan));
            }
        }

        Pair<RecipeIngredientCtrl, Parent> item = fxml.load(RecipeIngredientCtrl.class, mainCtrl.getBundle(),
                "client", "scenes", "RecipeIngredient.fxml");
        item.getKey().initialize(null, recipe, this::showIngredients);

        item.getValue().setUserData(item.getKey());
        ingredientsContainer.getChildren().add(item.getValue());
        item.getKey().startEditingFromCtrl();
    }

    /**
     * function to save the recipe
     */
    @FXML
    public void onSaveRecipe() {
        try {
            // Make sure the inputs are correct
            String name = nameTextField.getText().trim();
            boolean isCheap = cheapCheckBox.isSelected();
            boolean isFast = fastCheckBox.isSelected();
            boolean isVegan = veganCheckBox.isSelected();
            if (name.isBlank()) {
                mainCtrl.showError("Input Error", "Recipe name cannot be empty.");
                return;
            }

            if(recipeNameChecker(recipeService.getAllRecipes(), name, this.recipe)){
                mainCtrl.showError("Used Name",
                        "This recipe name is already in use, please choose another.");
                return;
            }

            int servings;
            try {
                servings = Integer.parseInt(servingsArea.getText().trim());
                if (servings < 1) {
                    mainCtrl.showError("Input Error", "Servings must be at least 1.");
                    return;
                }
            } catch (NumberFormatException e) {
                mainCtrl.showError("Input Error", "Servings must be a number.");
                return;
            }

            List<String> preparationSteps = Arrays.asList(
                    preparationsArea.getText().split("\\r?\\n"));
            if (preparationSteps.isEmpty() ||
                    (preparationSteps.size() == 1 && preparationSteps.get(0).isBlank())) {
                mainCtrl.showError("Input Error", "Preparation steps cannot be empty.");
                return;
            }

            Language language;
            System.out.println(languageChoise.getValue());
            if(languageChoise.getValue().equals("English")){
                language = Language.English;
            } else if(languageChoise.getValue().equals("Dutch")){
                language = Language.Dutch;
            } else if(languageChoise.getValue().equals("Polish")){
                language = Language.Polish;
            } else {
                mainCtrl.showError("Input Error", "There was no proper language selected");
                return;
            }

            isSaved = true;

            // Check if a recipe exists before adding the ingredients.
            if (recipe == null) {
                // If not first create the recipe.
                recipe = server.add(new Recipe(name, servings, preparationSteps, language, isCheap, isFast, isVegan));
            } else if (isCloneMode && recipe.getId() == 0) {
                recipe = server.add(new Recipe(name, servings, preparationSteps, language, isCheap, isFast, isVegan));
                isCloneMode = false;
            } else {
                // Update the existing recipe.
                recipe.setCheap(isCheap);
                recipe.setFast(isFast);
                recipe.setVegan(isVegan);
                recipe.setName(name);
                recipe.setServings(servings);
                recipe.setPreparationSteps(preparationSteps);
                recipe.setLanguage(language);
                recipe = server.updateRecipe(recipe);
            }

            // Now save all ingredients to the server.
            saveAllIngredientsToServer(recipe);

            appViewCtrl.loadRecipes();
            mainCtrl.showRecipe(recipe);

        } catch (Exception e) {
            mainCtrl.showError("Error",
                    "Could not save the recipe. There might be a problem with your server connection.");
        }
    }

    /**
     * Save all ingredients to the server.
     */
    private void saveAllIngredientsToServer(Recipe targetRecipe) {
        try {
            //Collect the ingredients.
            List<RecipeIngredient> ingredientsToSave = new ArrayList<>();

            for (javafx.scene.Node node : ingredientsContainer.getChildren()) {
                Object controller = node.getUserData();
                if (controller instanceof RecipeIngredientCtrl ctrl) {
                    RecipeIngredient ri = ctrl.getRecipeIngredient();

                    if (ri != null && ri.getIngredient() != null) {
                        if (ri.getRecipe() == null || ri.getRecipe().getId() != targetRecipe.getId()) {
                            ri.setRecipe(targetRecipe);
                        }
                        ingredientsToSave.add(ri);
                    }
                }
            }

            // Only delete old ingredients if we have new ones to save
            if (!ingredientsToSave.isEmpty()) {
                if (targetRecipe.getId() > 0) {
                    // Delete all existing ingredients to prevent duplicates
                    List<RecipeIngredient> existing = server.getRecipeIngredients(targetRecipe.getId());
                    if (existing != null) {
                        for (RecipeIngredient old : existing) {
                            server.deleteRecipeIngredient(old.getId());
                        }
                    }
                }

                // Input the recipes again
                for (RecipeIngredient ingredient : ingredientsToSave) {
                    RecipeIngredient fresh = new RecipeIngredient(
                            targetRecipe,
                            ingredient.getIngredient(),
                            ingredient.getInformalUnit(),
                            ingredient.getAmount(),
                            ingredient.getUnit()
                    );
                    server.addRecipeIngredient(fresh);
                }
            }
        } catch (Exception e) {
            System.out.println("There was an error in the saving of the ingredients.");
        }
    }

    /**
     * the function to clone the recipe.
     * @param originalRecipe the recipe information that needs be inputted for the clone.
     */
    public void clone(Recipe originalRecipe) {
        if (originalRecipe == null) return;

        // Clear the existing data
        clearForm();

        this.isCloneMode = true;

        // Set the values that have changed to the clone
        nameTextField.setText(originalRecipe.getName() + " - Clone");
        servingsArea.setText(String.valueOf(originalRecipe.getServings()));
        preparationsArea.setText(String.join("\n", originalRecipe.getPreparationSteps()));
        cheapCheckBox.setSelected(originalRecipe.isCheap());
        fastCheckBox.setSelected(originalRecipe.isFast());
        veganCheckBox.setSelected(originalRecipe.isVegan());
        try{
            languageChoise.setValue(originalRecipe.getLanguage().toString());
        } catch(Exception _){

        }


        this.recipe = new Recipe(
                (originalRecipe.getName() + " - Clone"),
                originalRecipe.getServings(),
                new ArrayList<>(originalRecipe.getPreparationSteps()),
                originalRecipe.getLanguage(),
                originalRecipe.isCheap(),
                originalRecipe.isFast(),
                originalRecipe.isVegan()
        );
        // Load and clone the ingredients
        cloneIngredients(originalRecipe);

        recipe = server.add(this.recipe);

        saveAllIngredientsToServer(recipe);
        showIngredients();
    }

    /**
     * Clears the form for fresh input
     */
    private void clearForm() {
        nameTextField.clear();
        servingsArea.clear();
        preparationsArea.clear();
        ingredientsContainer.getChildren().clear();
        recipe = null;
        isCloneMode = false;
        cheapCheckBox.setSelected(false);
        fastCheckBox.setSelected(false);
        veganCheckBox.setSelected(false);
    }

    /**
     * Clones ingredients from the original recipe.
     */
    private void cloneIngredients(Recipe originalRecipe) {
        if (originalRecipe == null || fxml == null) return;

        // Get ingredients from the original recipe
        List<RecipeIngredient> originalIngredients = server.getRecipeIngredients(originalRecipe.getId());

        if(languageChoise == null){
            return;
        }

        if (originalIngredients == null || originalIngredients.isEmpty()) {
            return;
        }

        // Clear current ingredients
        ingredientsContainer.getChildren().clear();

        // Clone each ingredient
        for (RecipeIngredient originalIngredient : originalIngredients) {
            RecipeIngredient clonedIngredient = new RecipeIngredient(
                    recipe,
                    originalIngredient.getIngredient(),
                    originalIngredient.getInformalUnit(),
                    originalIngredient.getAmount(),
                    originalIngredient.getUnit()
            );

            Pair<RecipeIngredientCtrl, Parent> item = fxml.load(RecipeIngredientCtrl.class, mainCtrl.getBundle(),
                    "client", "scenes", "RecipeIngredient.fxml");
            item.getKey().initialize(clonedIngredient, recipe, this::showIngredients);
            item.getValue().setUserData(item.getKey());

            ingredientsContainer.getChildren().add(item.getValue());
        }
    }

    /**
     * To cancel the add function
     */
    public void onCancel(){
        try{
            // if cancelled delete the recipe we created.
            if(recipe != null && !isCloneMode && recipe.getId() > 0){
                deleter(recipe.getId());
            }
        } catch(Exception e){
            System.out.println("Something went wrong");
        }

        if(mainCtrl.getFirstOpen()){
            mainCtrl.showAppView();
            appViewCtrl.loadRecipes();
            mainCtrl.showDefaultView();
        } else {
            mainCtrl.showAppView();
            appViewCtrl.loadRecipes();
        }
    }

    /**
     * function that removed the recipe id.
     * @param id the id of the recipe to be removed
     */
    public void deleter(long id){
        server.deleteRecipe(id);
    }

    /**
     * This function is to show the ingredients for the adding of recipes.
     */
    private void showIngredients(){
        if(recipe == null || fxml == null){
            return;
        }
        // first up clear the ingredient Container.
        ingredientsContainer.getChildren().clear();

        if (recipe.getId() > 0) {
            this.recipeIngredientList = server.getRecipeIngredients(recipe.getId());

            if(recipeIngredientList == null){
                recipeIngredientList = new ArrayList<>();
            }

            for(RecipeIngredient ri : recipeIngredientList){
                //To make sure the ingredients are set on the correct recipe.
                ri.setRecipe(recipe);

                Pair<RecipeIngredientCtrl, Parent> item = fxml.load(RecipeIngredientCtrl.class, mainCtrl.getBundle(),
                        "client", "scenes", "RecipeIngredient.fxml");
                item.getKey().initialize(ri, recipe, this::showIngredients);

                item.getValue().setUserData(item.getKey());

                ingredientsContainer.getChildren().add(item.getValue());
            }
        }
    }

    /**
     * function to get the is saved value
     * @return the is saved value
     */
    public boolean getIsSaved(){
        return this.isSaved;
    }

    /**
     * function to make sure isSaved is true and random values do not get deleted.
     */
    public void setIsSavedTrue(){
        this.isSaved = true;
    }

    /**
     * Function to get the recipe
     * @return the recipe of the addRecipeCtrl.
     */
    public Recipe getRecipe(){
        return this.recipe;
    }
}