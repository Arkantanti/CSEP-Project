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

public class  IngredientViewCtrl {

    @FXML
    private Button titleEditButton;
    @FXML
    private TextField nameTextField;
    @FXML
    private Label nameLabel;
    @FXML
    private Label fatLabel;
    @FXML
    private Label proteinLabel;
    @FXML
    private Label carbsLabel;
    @FXML
    private Label usedCountLabel;
    @FXML
    private Label kcalLabel;

    private MyFXML fxml;
    private final ServerUtils server;
    private boolean editing = false;
    private Ingredient ingredient;
    private final AppViewCtrl appViewCtrl;

    /**
     * Constructor for IngredientViewCtrl.
     *
     * @param server the server utility used for network communication
     */
    @Inject
    public IngredientViewCtrl(ServerUtils server, MainCtrl mainCtrl, Printer printer) {
        this.server = server;
        this.appViewCtrl = mainCtrl.getAppViewCtrl();
    }

    /**
     * The method that gets called internally when setting up the IngredientView.
     * This method initializes the base properties for the IngredientView
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
     * @param fxml the FXML loader
     */
    public void setIngredient(Ingredient ingredient, MyFXML fxml) {
        this.fxml = fxml;
        this.ingredient = ingredient;
        if (ingredient != null) {
            nameLabel.setText(ingredient.getName());
            fatLabel.setText(String.valueOf(ingredient.getFat()));
            proteinLabel.setText(String.valueOf(ingredient.getProtein()));
            carbsLabel.setText(String.valueOf(ingredient.getCarbs()));
            kcalLabel.setText(String.valueOf(ingredient.calculateCalories()));
            usedCountLabel.setText(String.valueOf(server.recipeCount(ingredient.getId())));
        }
    }


    /**
     * Handles the edit button click for the ingredient name.
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
     * Starts editing mode for the ingredient name.
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
     * Finishes editing mode for the ingredient name.
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


