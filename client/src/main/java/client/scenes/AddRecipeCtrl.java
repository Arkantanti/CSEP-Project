package client.scenes;

import client.MyFXML;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeCtrl {

    @FXML private Button titleEditButton;    // optional, can be unused
    @FXML private TextField nameTextField;
    @FXML private Label nameLabel;           // optional, you can hide label and use only TextField
    @FXML private VBox ingredientsContainer; // placeholder for future ingredient UI
    @FXML private Button ingredientAddButton;
    @FXML private VBox preparationsContainer;
    @FXML private Button preparationAddButton;
    @FXML private Button saveButton;

    private MyFXML fxml;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    // The recipe we are creating
    private Recipe recipe;

    /**
     * gne
     * @param server gen
     * @param mainCtrl gne
     */
    @Inject
    public AddRecipeCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * gne
     */
    @FXML
    public void initialize() {
        // Start in "edit name" mode: show the text field immediately for a new recipe
        if (nameTextField != null) {
            nameTextField.setVisible(true);
            nameTextField.setManaged(true);
        }
        if (nameLabel != null) {
            nameLabel.setVisible(false);
            nameLabel.setManaged(false);
        }
    }

    /**
     * Call this method when loading the AddRecipe view so we can keep a reference to the fxml loader.
     * Example: addRecipeCtrl.init(fxml);
     */
    public void init(MyFXML fxml) {
        this.fxml = fxml;
        // create an empty Recipe (use sensible defaults)
        this.recipe = new Recipe("", 1, new ArrayList<>());
        // ensure the preparation list is never null
        if (this.recipe.getPreparationSteps() == null) {
            this.recipe.setPreparationSteps(new ArrayList<>());
        }
        // optionally populate UI with any initial state (none for a new recipe)
        this.preparationsContainer.getChildren().clear();
    }

    /**
     * Add an empty preparation step (UI + model) and start editing it.
     * Bound in FXML to the "+ preparation" button: onAction="#onAddPreparationStep"
     */
    @FXML
    public void onAddPreparationStep(ActionEvent ev) {
        if (recipe == null) {
            // Defensive: ensure recipe and steps exist
            recipe = new Recipe("", 1, new ArrayList<>());
        }
        List<String> steps = recipe.getPreparationSteps();
        if (steps == null) {
            steps = new ArrayList<>();
            recipe.setPreparationSteps(steps);
        }

        steps.add("");                      // add blank step to model
        int index = steps.size() - 1;

        Pair<EditableItemCtrl, Parent> item = fxml.load(EditableItemCtrl.class,
                "client", "scenes", "EditableItem.fxml");

        EditableItemCtrl ctrl = item.getKey();
        ctrl.setText(""); // blank initial text

        // Bind UI to the underlying list + save callback on commit/delete
        ctrl.bindTo(
                steps,
                index,
                () -> {
                    // on non-empty commit or deletion we don't have to update server yet
                    // we just refresh the UI so indexes remain correct
                    loadPreparationSteps(recipe.getPreparationSteps());
                },
                true // new item (so empty commits remove themselves)
        );

        preparationsContainer.getChildren().add(item.getValue());
        ctrl.startEditingFromCtrl();
    }

    /**
     * Renders the current steps into the UI (clear and re-add)
     * Useful if steps were changed programmatically or after commits.
     */
    private void loadPreparationSteps(List<String> steps) {
        preparationsContainer.getChildren().clear();
        if (steps == null || fxml == null) return;
        for (int i = 0; i < steps.size(); i++) {
            String step = steps.get(i);
            Pair<EditableItemCtrl, Parent> item = fxml.load(EditableItemCtrl.class,
                    "client", "scenes", "EditableItem.fxml");
            EditableItemCtrl ctrl = item.getKey();
            ctrl.setText(step);
            int idx = i;
            ctrl.bindTo(steps, idx, () -> loadPreparationSteps(recipe.getPreparationSteps()), false);
            preparationsContainer.getChildren().add(item.getValue());
        }
    }

    /**
     * Save the new recipe to the server.
     * Bound to the "✓" save button: onAction="#saveRecipe"
     */
    @FXML
    public void saveRecipe(ActionEvent ev) {
        if (recipe == null) {
            recipe = new Recipe("", 1, new ArrayList<>());
        }

        // take the name from UI
        String name = (nameTextField != null) ? nameTextField.getText() : null;
        if (name == null || name.isBlank()) {
            // optionally show an error/alert to the user (omitted here)
            // but we set a default name to avoid server validation failure
            name = "Untitled recipe";
        }
        recipe.setName(name.trim());

        // sanitation: remove blank steps
        List<String> steps = recipe.getPreparationSteps();
        if (steps == null) {
            steps = new ArrayList<>();
            recipe.setPreparationSteps(steps);
        } else {
            steps.removeIf(s -> s == null || s.isBlank());
        }

        server.addRecipe(recipe);
        try {
            mainCtrl.showRecipe(recipe);
        } catch (Exception e) {

        }
    }
}
