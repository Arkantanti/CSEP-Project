package client.scenes;

import com.google.inject.Inject;
import client.utils.ServerUtils;
import commons.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import java.util.Arrays;
import java.util.List;

public class AddRecipeCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField recipeNameField;

    @FXML
    private TextArea ingredientsArea;

    @FXML
    private TextArea instructionsArea;

    /**
     *  gne
     * @param server gne
     * @param mainCtrl gne
     */
    @Inject
    public AddRecipeCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     *  gne
     */
    @FXML
    public void onSaveRecipe() {
        try {
            String name = recipeNameField.getText().trim();
            int servings = 1;
            List<String> ingredients = Arrays.asList(ingredientsArea.getText().split("\\r?\\n"));
            List<String> steps = Arrays.asList(instructionsArea.getText().split("\\r?\\n"));

            if (name.isBlank() || ingredients.isEmpty() || steps.isEmpty()) {
                showError("Invalid input", "Please fill all fields correctly.");
                return;
            }

            Recipe recipe = new Recipe(name, servings, steps);
            Recipe savedRecipe = server.add(recipe);

            clearFields();
            mainCtrl.showRecipe(savedRecipe);
        } catch (Exception e) {
            showError("Error", "Could not save recipe. Check your input or server connection.");
        }
    }

    /**
     *  gne
     */
    @FXML
    public void onCancel() {
        clearFields();
        mainCtrl.showAppView();
    }

    /**
     * gne
     */
    private void clearFields() {
        recipeNameField.clear();
        ingredientsArea.clear();
        instructionsArea.clear();
    }

    /**
     * gne
     * @param header gne
     * @param content gne
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}