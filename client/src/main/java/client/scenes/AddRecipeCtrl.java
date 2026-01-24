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
     * The constructor for the add recipeController
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
    private void onAddRecipeIngredient() {
        if (recipe == null) {
            if (!initializeRecipe()) {
                return;
            }
        }
        addIngredientRow();
    }

    /**
     * Initializes the recipe if it doesn't exist.
     * @return true if successful, false if validation failed.
     */
    private boolean initializeRecipe() {
        String name = nameTextField.getText().trim();
        if (name.isEmpty()) {
            name = "New Recipe";
        }

        if (!validateRecipe(name)) {
            return false;
        }

        int servings = parseServings();
        List<String> steps = getPreparationSteps();
        Language language = parseLanguage();

        createAndSetRecipe(name, servings, steps, language);
        return true;
    }

    /**
     * Validates recipe name and language selection.
     * @param name The name to check.
     * @return true if valid.
     */
    private boolean validateRecipe(String name) {
        if (recipeService.recipeNameChecker(recipeService.getAllRecipes(), name, this.recipe)) {
            mainCtrl.showError("Name Used.", "This name is already in use.");
            return false;
        }
        if (languageChoise.getValue() == null) {
            mainCtrl.showError("No language selected", "Choose a valid language");
            return false;
        }
        return true;
    }

    /**
     * Parses the servings input (Silent version for Drafts).
     * @return The number of servings, default 1 on error.
     */
    private int parseServings() {
        try {
            return Integer.parseInt(servingsArea.getText().trim());
        } catch (Exception ignored) {
            System.out.println("The value in servingsArea was invalid.");
            return 1;
        }
    }

    /**
     * Retrieves preparation steps from the text area.
     * @return List of steps.
     */
    private List<String> getPreparationSteps() {
        if (!preparationsArea.getText().isEmpty()) {
            return Arrays.asList(preparationsArea.getText().split("\\r?\\n"));
        }
        return new ArrayList<>();
    }

    /**
     * Parses the selected language.
     * @return The selected Language enum.
     */
    private Language parseLanguage() {
        String selected = languageChoise.getValue();
        if ("English".equals(selected)) {
            return Language.English;
        }
        if ("Dutch".equals(selected)) {
            return Language.Dutch;
        }
        if ("Polish".equals(selected)) {
            return Language.Polish;
        }
        return null;
    }

    /**
     * Creates the recipe object and sets it to the class field.
     */
    private void createAndSetRecipe(String name, int serv, List<String> steps, Language lang) {
        boolean isCheap = cheapCheckBox.isSelected();
        boolean isFast = fastCheckBox.isSelected();
        boolean isVegan = veganCheckBox.isSelected();

        Recipe newRecipe = new Recipe(name, serv, steps, lang, isCheap, isFast, isVegan);

        if (isCloneMode) {
            recipe = newRecipe;
        } else {
            recipe = server.add(newRecipe);
        }
    }

    /**
     * Loads and adds the ingredient UI row.
     */
    private void addIngredientRow() {
        Pair<RecipeIngredientCtrl, Parent> item = fxml.load(RecipeIngredientCtrl.class,
                mainCtrl.getBundle(), "client", "scenes", "RecipeIngredient.fxml");

        item.getKey().initialize(null, recipe, this::refreshIngredients);
        item.getValue().setUserData(item.getKey());
        ingredientsContainer.getChildren().add(item.getValue());
        item.getKey().startEditingFromCtrl();
    }

    /**
     * Helper to refresh ingredients only if the recipe is persisted.
     * Prevents wiping the UI for local/unsaved recipes.
     */
    private void refreshIngredients() {
        if (recipe != null && recipe.getId() > 0) {
            showIngredients();
        }
    }

    /**
     * function to save the recipe
     */
    @FXML
    public void onSaveRecipe() {
        try {
            String name = nameTextField.getText().trim();
            if (!validateName(name)) return;

            // Updated call to the renamed method
            int servings = validateServings();
            if (servings < 1) return;

            List<String> preparationSteps = getValidSteps();
            if (preparationSteps == null) return;

            Language language = getValidLanguage();
            if (language == null) return;

            saveOrUpdateRecipe(name, servings, preparationSteps, language);

            // Now save all ingredients to the server.
            saveAllIngredientsToServer(recipe);

            appViewCtrl.loadRecipes();
            mainCtrl.showRecipe(recipe);

        } catch (Exception e) {
            e.printStackTrace();
            mainCtrl.showError("Error",
                    "Could not save the recipe." +
                            " There might be a problem with your server connection.");
        }
    }

    /**
     * Validates the recipe name.
     * @param name The name to check.
     * @return true if valid, false otherwise.
     */
    private boolean validateName(String name) {
        if (name.isBlank()) {
            mainCtrl.showError("Input Error", "Recipe name cannot be empty.");
            return false;
        }
        if (recipeService.recipeNameChecker(recipeService.getAllRecipes(), name, this.recipe)) {
            mainCtrl.showError("Used Name",
                    "This recipe name is already in use, please choose another.");
            return false;
        }
        return true;
    }

    /**
     * Parses and validates the servings input (Strict version for Save).
     * RENAMED to avoid conflict with parseServings().
     * @return The number of servings, or -1 if invalid.
     */
    private int validateServings() {
        try {
            int servings = Integer.parseInt(servingsArea.getText().trim());
            if (servings < 1) {
                mainCtrl.showError("Input Error", "Servings must be at least 1.");
                return -1;
            }
            return servings;
        } catch (NumberFormatException e) {
            mainCtrl.showError("Input Error", "Servings must be a number.");
            return -1;
        }
    }

    /**
     * Retrieves and validates preparation steps.
     * @return The list of steps, or null if invalid.
     */
    private List<String> getValidSteps() {
        List<String> steps = Arrays.asList(preparationsArea.getText().split("\\r?\\n"));
        if (steps.isEmpty() || (steps.size() == 1 && steps.get(0).isBlank())) {
            mainCtrl.showError("Input Error", "Preparation steps cannot be empty.");
            return null;
        }
        return steps;
    }

    /**
     * Retrieves and validates the language selection.
     * @return The selected Language, or null if invalid.
     */
    private Language getValidLanguage() {
        String selected = languageChoise.getValue();
        if ("English".equals(selected)) return Language.English;
        if ("Dutch".equals(selected)) return Language.Dutch;
        if ("Polish".equals(selected)) return Language.Polish;

        mainCtrl.showError("Input Error", "There was no proper language selected");
        return null;
    }

    /**
     * Handles the logic for creating or updating the recipe on the server.
     */
    private void saveOrUpdateRecipe(String name, int servings,
                                    List<String> steps, Language lang) {
        boolean isCheap = cheapCheckBox.isSelected();
        boolean isFast = fastCheckBox.isSelected();
        boolean isVegan = veganCheckBox.isSelected();
        isSaved = true;

        if (recipe == null || recipe.getId() == 0) {
            recipe = server.add(new Recipe(name, servings, steps, lang,
                    isCheap, isFast, isVegan));
            if (isCloneMode) {
                isCloneMode = false;
            }
        } else {
            recipe.setCheap(isCheap);
            recipe.setFast(isFast);
            recipe.setVegan(isVegan);
            recipe.setName(name);
            recipe.setServings(servings);
            recipe.setPreparationSteps(steps);
            recipe.setLanguage(lang);
            recipe = server.updateRecipe(recipe);
        }
    }

    /**
     * Save all ingredients to the server.
     */
    private void saveAllIngredientsToServer(Recipe targetRecipe) {
        try {
            List<RecipeIngredient> ingredientsToSave = collectIngredientsFromUI(targetRecipe);

            // Only delete old ingredients if we have new ones to save
            if (!ingredientsToSave.isEmpty()) {
                deleteOldIngredients(targetRecipe);
                saveNewIngredients(targetRecipe, ingredientsToSave);
            }
        } catch (Exception e) {
            System.out.println("There was an error in the saving of the ingredients.");
        }
    }

    /**
     * Collects valid ingredients from the UI components.
     * @param targetRecipe The recipe to link the ingredients to.
     * @return A list of valid RecipeIngredients.
     */
    private List<RecipeIngredient> collectIngredientsFromUI(Recipe targetRecipe) {
        List<RecipeIngredient> ingredientsToSave = new ArrayList<>();

        for (javafx.scene.Node node : ingredientsContainer.getChildren()) {
            Object controller = node.getUserData();
            if (controller instanceof RecipeIngredientCtrl ctrl) {
                RecipeIngredient ri = ctrl.getRecipeIngredient();

                if (ri != null && ri.getIngredient() != null) {
                    if (ri.getRecipe() == null
                            || ri.getRecipe().getId() != targetRecipe.getId()) {
                        ri.setRecipe(targetRecipe);
                    }
                    ingredientsToSave.add(ri);
                }
            }
        }
        return ingredientsToSave;
    }

    /**
     * Deletes existing ingredients for the recipe from the server.
     * @param targetRecipe The recipe to clean up ingredients for.
     */
    private void deleteOldIngredients(Recipe targetRecipe) {
        if (targetRecipe.getId() > 0) {
            // Delete all existing ingredients to prevent duplicates
            List<RecipeIngredient> existing
                    = server.getRecipeIngredients(targetRecipe.getId());
            if (existing != null) {
                for (RecipeIngredient old : existing) {
                    server.deleteRecipeIngredient(old.getId());
                }
            }
        }
    }

    /**
     * Uploads the new list of ingredients to the server.
     * @param targetRecipe The recipe the ingredients belong to.
     * @param ingredientsToSave The list of ingredients to save.
     */
    private void saveNewIngredients(Recipe targetRecipe, List<RecipeIngredient> ingredientsToSave) {
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
        } catch(Exception ignored){
            // Replaced '_' with 'ignored'
        }


        // Create LOCAL recipe only. Do NOT save to server yet.
        this.recipe = new Recipe(
                (originalRecipe.getName() + " - Clone"),
                originalRecipe.getServings(),
                new ArrayList<>(originalRecipe.getPreparationSteps()),
                originalRecipe.getLanguage(),
                originalRecipe.isCheap(),
                originalRecipe.isFast(),
                originalRecipe.isVegan()
        );

        // Load and clone the ingredients into the UI
        cloneIngredients(originalRecipe);

        // Do NOT call saveAllIngredientsToServer here.
        // Do NOT call showIngredients here (it would clear the UI since ID is 0).
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
        List<RecipeIngredient> originalIngredients
                = server.getRecipeIngredients(originalRecipe.getId());

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

            Pair<RecipeIngredientCtrl, Parent> item =
                    fxml.load(RecipeIngredientCtrl.class, mainCtrl.getBundle(),
                            "client", "scenes", "RecipeIngredient.fxml");

            // Use refreshIngredients to protect local data
            item.getKey().initialize(clonedIngredient, recipe, this::refreshIngredients);
            item.getValue().setUserData(item.getKey());

            ingredientsContainer.getChildren().add(item.getValue());
        }
    }

    /**
     * To cancel the add function
     */
    public void onCancel(){
        try{
            // If cancelled, delete the recipe ONLY if it was actually persisted
            // and we are not in clone mode (which shouldn't have ID > 0 anyway until saved).
            // With the new fix, recipe.getId() is 0 for unsaved drafts, so this is safe.
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
     * Only works for recipes that exist on the server.
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

                Pair<RecipeIngredientCtrl, Parent> item
                        = fxml.load(RecipeIngredientCtrl.class, mainCtrl.getBundle(),
                        "client", "scenes", "RecipeIngredient.fxml");
                // Use the safe refresh method
                item.getKey().initialize(ri, recipe, this::refreshIngredients);

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