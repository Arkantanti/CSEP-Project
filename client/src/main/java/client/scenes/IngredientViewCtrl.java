package client.scenes;

import client.MyFXML;
import client.utils.Printer;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class IngredientViewCtrl {

    @FXML
    private Button titleEditButton;
    @FXML
    private TextField nameTextField;
    @FXML
    private Label nameLabel;

    private MyFXML fxml;
    private final ServerUtils server;
    private boolean editing = false;
    private final MainCtrl mainCtrl;
    private Ingredient ingredient;

    /**
     * Constructor for RecipeViewCtrl.
     *
     * @param server the server utility used for network communication
     */
    @Inject
    public IngredientViewCtrl(ServerUtils server, MainCtrl mainCtrl, Printer printer) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * The method that gets called internally when setting up the RecipeView.
     * This method initializes the base properties for the RecipeView
     */
    @FXML
    public void initialize() {
        // default state is label with text
        nameTextField.setVisible(false);
        nameTextField.setManaged(false);
    }

    /**
     * Sets the ingredient to display in this view.
     *
     * @param ingredient the ingredient to display
     * @param fxml   the FXML loader for loading EditableItem components
     */
    public void setIngredient(Ingredient ingredient, MyFXML fxml) {
        this.fxml = fxml;
        this.ingredient = ingredient;
        if (ingredient != null) {
            nameLabel.setText(ingredient.getName());
        }
    }

    /**
     * Handles the edit button click for the recipe title.
     * Toggles between editing and viewing mode.
     */
    @FXML
    private void onEditClicked() {
        if (!editing) {
            startEditing();
        } else {
            finishEditing();
        }
    }

    /**
     * Starts editing mode for the recipe title.
     * Shows the text field and hides the label.
     */
    private void startEditing() {
        editing = true;
        nameTextField.setText(nameLabel.getText());

        nameLabel.setVisible(false);
        nameLabel.setManaged(false);
        nameTextField.setVisible(true);
        nameTextField.setManaged(true);

        nameTextField.requestFocus();
        nameTextField.selectAll();

        titleEditButton.setText("✔");
        titleEditButton.setTextFill(Color.GREEN);
    }

    /**
     * Finishes editing mode for the recipe title.
     * Shows the label and hides the text field.
     */
    private void finishEditing() {
        editing = false;
        String newName = nameTextField.getText();

        if (newName != null && !newName.isBlank()) {
            nameLabel.setText(newName.trim());
            // update new title to the server.
            if (ingredient != null) {
                ingredient.setName(newName);
                server.updateIngredient(ingredient);
                appViewCtrl.loadRecipes();
            }
        }

        nameTextField.setVisible(false);
        nameTextField.setManaged(false);

        nameLabel.setVisible(true);
        nameLabel.setManaged(true);

        titleEditButton.setText("✏");
        titleEditButton.setTextFill(Color.web("#1e00ff"));
    }
}


