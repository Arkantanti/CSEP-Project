package client.scenes;

import client.MyFXML;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.List;

public class RecipeViewCtrl {

    @FXML
    public Button titleEditButton;
    @FXML
    public TextField nameTextField;
    @FXML
    public Label nameLabel;
    @FXML
    public VBox ingredientsContainer;
    @FXML
    public Button ingredientAddButton;
    @FXML
    public VBox preparationsContainer;
    @FXML
    public Button preparationAddButton;

    private MyFXML fxml;

    /**
     * Sets the recipe to display in this view.
     *
     * @param recipe the recipe to display
     * @param fxml   the FXML loader for loading EditableItem components
     */
    public void setRecipe(Recipe recipe, MyFXML fxml) {
        this.fxml = fxml;
        if (recipe != null) {
            nameLabel.setText(recipe.getName());
            loadIngredients(recipe.getIngredients());
            loadPreparationSteps(recipe.getPreparationSteps());
        }
    }

    /**
     * Loads ingredients into the ingredients container using EditableItem components.
     *
     * @param ingredients the list of recipe ingredients to display
     */
    private void loadIngredients(List<RecipeIngredient> ingredients) {
        ingredientsContainer.getChildren().clear();
        if (ingredients == null || fxml == null) {
            return;
        }
        for (RecipeIngredient ri : ingredients) {
            String text = ri.getIngredient().getName();
            if (ri.getAmount() > 0) {
                text += " (" + ri.getAmount() + " "
                        + (ri.getUnit() != null ? ri.getUnit() : "") + ")";
            }
            if (ri.getInformalUnit() != null && !ri.getInformalUnit().isEmpty()) {
                text = ri.getInformalUnit() + " " + text;
            }
            Pair<EditableItemCtrl, Parent> item = fxml.load(EditableItemCtrl.class,
                    "client", "scenes", "EditableItem.fxml");
            item.getKey().setText(text);
            ingredientsContainer.getChildren().add(item.getValue());
        }
    }

    /**
     * Loads preparation steps into the preparations container using EditableItem components.
     *
     * @param steps the list of preparation steps to display
     */
    private void loadPreparationSteps(List<String> steps) {
        preparationsContainer.getChildren().clear();
        if (steps == null || fxml == null) {
            return;
        }
        for (String step : steps) {
            Pair<EditableItemCtrl, Parent> item = fxml.load(EditableItemCtrl.class,
                    "client", "scenes", "EditableItem.fxml");
            item.getKey().setText(step);
            preparationsContainer.getChildren().add(item.getValue());
        }
    }
}
