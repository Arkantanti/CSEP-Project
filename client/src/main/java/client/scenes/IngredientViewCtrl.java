package client.scenes;

import client.MyFXML;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Allergen;
import commons.Ingredient;
import commons.IngredientCategory;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.converter.DoubleStringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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
    @FXML
    private FlowPane hboxAllergens;
    @FXML
    private Button addAllergenButton;
    @FXML
    private Label categoryLabel;
    @FXML
    private ComboBox<IngredientCategory> categoryComboBox;

    private final ServerUtils server;
    private boolean editing = false;
    private Ingredient ingredient;
    private final AppViewCtrl appViewCtrl;
    private final MainCtrl mainCtrl;

    private ContextMenu allergenMenu;
    private final Set<Allergen> selectedAllergens;

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
        this.selectedAllergens = new HashSet<>();
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

        // default state show category label not combox box for selecting category
        categoryComboBox.setVisible(false);
        categoryComboBox.setManaged(false);
        categoryComboBox.getItems().setAll(IngredientCategory.values());
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
     * @param fxml the FXML loader
     */
    public void setIngredient(Ingredient ingredient, MyFXML fxml) {
        this.ingredient = ingredient;
        if (ingredient != null) {
            nameLabel.setText(ingredient.getName());
            fatTf.setText(String.format(Locale.US, "%.2f", ingredient.getFat()));
            proteinTf.setText(String.format(Locale.US, "%.2f", ingredient.getProtein()));
            carbsTf.setText(String.format(Locale.US, "%.2f", ingredient.getCarbs()));
            kcalLabel.setText(String.format(Locale.US, "%.0f kcal/100g",ingredient.calculateCalories()*100));
            usedCountLabel.setText(String.valueOf(server.recipeCount(ingredient.getId())));

            for(Allergen allergen : ingredient.getAllergens()) {
                Label label = new Label(allergen.getDisplayName());
                label.getStyleClass().add("allergen-label");
                label.setStyle("-fx-background-color:" + allergen.getColor()+";");
                hboxAllergens.getChildren().addFirst(label);
                selectedAllergens.add(allergen);
            }
            allergenMenu.getItems().forEach(item -> {
                CheckMenuItem c = (CheckMenuItem) item;
                if (selectedAllergens.stream()
                        .map(Allergen::getDisplayName).toList().contains(c.getText())) {
                    c.setSelected(true);
                }
            });

            String categoryName = ingredient.getCategory().name();
            categoryLabel.setText(categoryName.charAt(0) + categoryName.substring(1).toLowerCase());
            categoryComboBox.setValue(ingredient.getCategory());

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
     * Shows the category combo box and hides the category label.
     */
    private void startEditing() {
        editing = true;
        nameTextField.setText(nameLabel.getText());

        nameLabel.setVisible(false);
        nameLabel.setManaged(false);
        nameTextField.setVisible(true);
        nameTextField.setManaged(true);

        // show category combo box and hide category label
        categoryLabel.setVisible(false);
        categoryLabel.setManaged(false);
        categoryComboBox.setVisible(true);
        categoryComboBox.setManaged(true);

        nameTextField.requestFocus();
        nameTextField.selectAll();

        titleEditButton.setText("✔");
        titleEditButton.setTextFill(Color.GREEN);
    }

    /**
     * Finishes editing mode for the ingredient name.
     * Shows the label and hides the text field.
     * Shows the category label and hides the category combo box.
     */
    private void finishEditing() {
        editing = false;
        String newName = nameTextField.getText();

        if (newName != null && !newName.isBlank()) {
            nameLabel.setText(newName.trim());
            // update new title to the server.
            if (ingredient != null) {
                ingredient.setName(newName);
                if (categoryComboBox.getValue() != null) {
                    ingredient.setCategory(categoryComboBox.getValue());
                }
                server.updateIngredient(ingredient);
                appViewCtrl.loadIngredients();
            }
        }

        nameTextField.setVisible(false);
        nameTextField.setManaged(false);

        nameLabel.setVisible(true);
        nameLabel.setManaged(true);

        // Hide category combo box
        categoryComboBox.setVisible(false);
        categoryComboBox.setManaged(false);

        if (ingredient != null && ingredient.getCategory() != null) {
            String categoryName = ingredient.getCategory().name();
            categoryLabel.setText(categoryName.charAt(0) + categoryName.substring(1).toLowerCase());
        } else {
            categoryLabel.setText("Uncategorized");
        }

        // show category label
        categoryLabel.setVisible(true);
        categoryLabel.setManaged(true);

        titleEditButton.setText("✏");
        titleEditButton.setTextFill(Color.web("#1e00ff"));
    }
    /**
     * Uses ServerUtils to delete an ingredient.
     */
    public void deleteIngredient(){
        long count = server.recipeCount(ingredient.getId());
        boolean delete = true;
        if(count > 0){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Ingredient");
            alert.setHeaderText(null);
            String recipesPart = count>1 ? " different recipes" : " recipe";
            String message = ingredient.getName()+" is used in "+count+recipesPart+
                    ". Are you sure you want to delete it?";
            alert.setContentText(message);

            Optional<ButtonType> result = alert.showAndWait();
            delete = !(result.isPresent() && result.get() == ButtonType.CANCEL);
        }
        if(delete) {
            try {
                server.deleteIngredient(this.ingredient.getId());
                appViewCtrl.loadIngredients();
            } catch (Exception e) {
                System.out.println("something went wrong.");
            }
            mainCtrl.showDefaultView();
        }
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
        hboxAllergens.getChildren().addFirst(label);
        ingredient.setAllergens(selectedAllergens);
        server.updateIngredient(ingredient);
    }

    /**
     * Removes an allergen.
     * @param a Allergen reference.
     */
    private void removeChip(Allergen a) {
        selectedAllergens.remove(a);

        hboxAllergens.getChildren().removeIf(node ->
                node instanceof Label &&
                        ((Label) node).getText().equals(a.getDisplayName())
        );
        ingredient.setAllergens(selectedAllergens);
        server.updateIngredient(ingredient);
    }

}




