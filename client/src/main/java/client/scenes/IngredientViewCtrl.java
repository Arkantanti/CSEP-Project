package client.scenes;

import client.MyFXML;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import javafx.util.converter.DoubleStringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class  IngredientViewCtrl {

    @FXML
    private Button titleEditButton;
    @FXML
    private TextField nameTextField;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField fatTf;
    @FXML
    private TextField proteinTf;
    @FXML
    private TextField carbsTf;
    @FXML
    private Label usedCountLabel;
    @FXML
    private Label kcalLabel;

    private final ServerUtils server;
    private boolean editing = false;
    private Ingredient ingredient;
    private final AppViewCtrl appViewCtrl;
    private final MainCtrl mainCtrl;

    /**
     * Constructor for IngredientViewCtrl.
     *
     * @param server the server utility used for network communication
     */
    @Inject
    public IngredientViewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.appViewCtrl = mainCtrl.getAppViewCtrl();
        this.mainCtrl = mainCtrl;
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
        List<TextField> fields = List.of(proteinTf,carbsTf,fatTf);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("(\\d+)?(\\.\\d*)?") ? change : null;
        };



        for (TextField tf : fields) {
            TextFormatter<Double> formatter =
                    new TextFormatter<>(new DoubleStringConverter(), null, filter);
            tf.setTextFormatter(formatter);
            tf.focusedProperty().addListener((obs, was, focused) -> {
                if (!focused) this.onStopEditing(tf);
            });
            tf.setOnAction(e -> this.onStopEditing(tf));
        }
    }

    /**
     * Sets the ingredient to display in this view.
     *
     * @param ingredient the ingredient to display
     * @param fxml the FXML loader
     */
    public void setIngredient(Ingredient ingredient, MyFXML fxml) {
        this.ingredient = ingredient;
        if (ingredient != null) {
            nameLabel.setText(ingredient.getName());
            fatTf.setText(String.format(Locale.US, "%.2f", ingredient.getFat()));
            proteinTf.setText(String.format(Locale.US, "%.2f", ingredient.getProtein()));
            carbsTf.setText(String.format(Locale.US, "%.2f", ingredient.getCarbs()));
            kcalLabel.setText(String.format(Locale.US,
                    "%.0f kcal/100g",ingredient.calculateCalories()*100));
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
                appViewCtrl.loadIngredients();
            }
        }

        nameTextField.setVisible(false);
        nameTextField.setManaged(false);

        nameLabel.setVisible(true);
        nameLabel.setManaged(true);

        titleEditButton.setText("✏");
        titleEditButton.setTextFill(Color.web("#1e00ff"));
    }
    /**
     * Uses ServerUtils to delete an ingredient.
     */
    public void deleteIngredient(){
        try{
            server.deleteIngredient(this.ingredient.getId());
            appViewCtrl.loadIngredients();
        } catch (Exception e){
            System.out.println("something went wrong.");
        }
        mainCtrl.showDefaultView();
    }

    /**
     * Function that deals with the end of editing of a textField
     * @param tf TextField reference.
     */
    public void onStopEditing(TextField tf) {
        if(tf.getText().isEmpty()) {
            tf.setText("0");
        } else {
            double newValue = new BigDecimal(tf.getText())
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            switch(tf.getId()) {
                case "proteinTf":
                    ingredient.setProtein(newValue);
                    break;
                case "carbsTf":
                    ingredient.setCarbs(newValue);
                    break;
                case "fatTf":
                    ingredient.setFat(newValue);
            }
            server.updateIngredient(ingredient);
            appViewCtrl.loadIngredients();
        }
    }
}




