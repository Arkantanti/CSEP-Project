package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class AddIngredientCtrl {

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

    private final ServerUtils server;
    private Ingredient ingredient;
    private final AppViewCtrl appViewCtrl;
    private final MainCtrl mainCtrl;

    /**
     * Constructor for IngredientViewCtrl.
     *
     * @param server the server utility used for network communication
     */
    @Inject
    public AddIngredientCtrl(ServerUtils server, MainCtrl mainCtrl) {
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
                if (!focused) this.onStopEditingFloat(tf);
            });
            tf.setOnAction(e -> this.onStopEditingFloat(tf));
        }

        nameTextField.focusedProperty().addListener((obs, was, focused) -> {
            if (!focused) this.onStopEditingText();
        });
        nameTextField.setOnAction(e -> this.onStopEditingText());

        setIngredient(new Ingredient("NewIngredient",0,0,0));
    }

    /**
     * Sets the ingredient to display in this view.
     *
     * @param ingredient the ingredient to display
     */
    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        if (ingredient != null) {
            nameTextField.setText(ingredient.getName());
            fatTf.setText(String.format(Locale.US, "%.2f", ingredient.getFat()));
            proteinTf.setText(String.format(Locale.US, "%.2f", ingredient.getProtein()));
            carbsTf.setText(String.format(Locale.US, "%.2f", ingredient.getCarbs()));
        }
    }

    /**
     * Function that handles the end of editing of name label text field.
     */
    public void onStopEditingText() {
        if(nameTextField.getText().isEmpty()) {
            nameTextField.setText("NewIngredient");
        } else {
            ingredient.setName(nameTextField.getText());
        }
    }


    /**
     * Function that deals with the end of editing of a text fields with floats.
     * @param tf TextField reference.
     */
    public void onStopEditingFloat(TextField tf) {
        if(tf.getText().isEmpty()) {
            tf.setText("0");
        } else {
            double newValue = new BigDecimal(tf.getText())
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            tf.setText(String.format(Locale.US, "%.2f", newValue));
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
        }
    }

    /**
     * Exits the ingredient adding view.
     */
    public void onCancel(){
        mainCtrl.showDefaultView();
    }

    /**
     * Saves the ingredient to the database.
     */
    @FXML
    public void onSave() {
        this.ingredient = server.addIngredient(ingredient);
        appViewCtrl.loadIngredients();
        mainCtrl.showIngredient(ingredient);
    }
}




