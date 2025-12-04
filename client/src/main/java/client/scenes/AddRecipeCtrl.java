package client.scenes;

import com.google.inject.Inject;
import client.utils.ServerUtils;
import com.sun.javafx.scene.control.IntegerField;
import commons.Ingredient;
import commons.Recipe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

public class AddRecipeCtrl {
    @FXML
    private Button titleEditButton;
    @FXML
    private Label nameLabel;
    @FXML
    private VBox ingredientsContainer;
    @FXML
    private Button ingredientAddButton;
    @FXML
    private VBox preparationsContainer;
    @FXML
    private Button saveButton;
    
    private ActionEvent ae;
    /**
     * gne
     */
    @FXML
    public void initialize() {
    }

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField servingsArea;

    @FXML
    private TextArea preparationsArea;

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

            String name = nameTextField.getText().trim();
            int servings = Integer.parseInt(servingsArea.getText().trim());
            List<String> preperationSteps = Arrays.asList(preparationsArea.getText().split("\\r?\\n"));

            if (name.isBlank() || preperationSteps.isEmpty() || servings < 0) {
                showError("Invalid input", "Please fill all fields correctly.");
                return;
            }

            Recipe recipe = new Recipe(name, servings, preperationSteps);
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
        nameTextField.clear();
        servingsArea.clear();
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