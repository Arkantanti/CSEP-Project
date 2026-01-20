package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Allergen;
import commons.Ingredient;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.util.converter.DoubleStringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
    @FXML
    private FlowPane fpAllergens;
    @FXML
    private Button addAllergenButton;

    private final ServerUtils server;
    private Ingredient ingredient;
    private final AppViewCtrl appViewCtrl;
    private final MainCtrl mainCtrl;

    private boolean separateWindow;
    private boolean ingredientSaved;

    private ContextMenu allergenMenu;
    private final Set<Allergen> selectedAllergens;

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
        selectedAllergens = new HashSet<>();
    }

    /**
     * The method that gets called internally when setting up the IngredientView.
     * This method initializes the base properties for the IngredientView
     */
    @FXML
    public void initialize(boolean separateWindow) {
        this.separateWindow = separateWindow;
        this.ingredientSaved = false;

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

        setIngredient(new Ingredient("NewIngredient",0,0,0, Set.of()));

        allergenMenu = new ContextMenu();

        for (Allergen a : Allergen.values()) {
            CheckMenuItem item = new CheckMenuItem(a.getDisplayName());
            item.setOnAction(e -> {
                if (item.isSelected()) {
                    addChip(a);
                } else {
                    removeChip(a);
                }
            });
            allergenMenu.getItems().add(item);
        }
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
        if(separateWindow) {
            mainCtrl.closeAddIngredientWindow();
        } else {
            mainCtrl.showDefaultView();
        }
    }

    /**
     * Saves the ingredient to the database.
     */
    @FXML
    public void onSave() {
        this.ingredient = server.addIngredient(ingredient);
        ingredientSaved = true;
        if(separateWindow) {
            mainCtrl.closeAddIngredientWindow();
        } else {
            appViewCtrl.loadIngredients();
            mainCtrl.showIngredient(ingredient);
        }
    }

    public boolean getIngredientSaved() {
        return ingredientSaved;
    }

    public Ingredient getIngredient(){
        return ingredient;
    }

    /**
     * Adds a new allergen and refreshes the view.
     */
    public void addAllergen() {
        allergenMenu.show(addAllergenButton, Side.BOTTOM, 0, 0);
    }


    /**
     * Adds a new allergen
     * @param a Allergen reference to add.
     */
    private void addChip(Allergen a) {
        if (!selectedAllergens.add(a)) return;

        Label label = new Label(a.getDisplayName());
        label.getStyleClass().add("allergen-label");
        label.setStyle("-fx-background-color:" + a.getColor()+";");
        fpAllergens.getChildren().addFirst(label);
        ingredient.setAllergens(selectedAllergens);
    }

    /**
     * Removes an allergen.
     * @param a Allergen reference.
     */
    private void removeChip(Allergen a) {
        selectedAllergens.remove(a);

        fpAllergens.getChildren().removeIf(node ->
                node instanceof Label &&
                        ((Label) node).getText().equals(a.getDisplayName())
        );
        ingredient.setAllergens(selectedAllergens);
    }
}




