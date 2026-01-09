package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.List;

public class EditableItemCtrl {

    @FXML
    private Label textLabel;
    @FXML
    private TextField textField;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private boolean editing = false;
    private String labelText;

    private List<String> parentList;
    private int listIndex;
    private Runnable onChange;
    private boolean newItem = false;

    /**
     * Called by parent controller after loading the component
     */
    public void bindTo(
            List<String> parentList,
            int listIndex,
            Runnable onChange,
            boolean newItem) {
        this.parentList = parentList;
        this.listIndex = listIndex;
        this.onChange = onChange;
        this.newItem = newItem;
    }

    /**
     * Internally run method that initializes an EditableItem component
     */
    @FXML
    public void initialize() {
        // default state is label with text
        textField.setVisible(false);
        textField.setManaged(false);

        textField.setMinWidth(Region.USE_PREF_SIZE);
        textField.setMaxWidth(Region.USE_PREF_SIZE);
    }

    /**
     * Sets the text for the {@code textLabel}
     *
     * @param text the text which will be set to the {@code textLabel}
     */
    public void setText(String text) {
        labelText = text;
        if (textLabel != null) {
            textLabel.setText(text);
        }
    }

    public String getText() {
        return labelText;
    }

    /**
     * The onAction method for when the {@code editButton is clicked}
     * When button is not in edit mode the {@code textLabel} turns into a {@code textField}
     * and the editButton turns into a green check button. When the button is
     * in edit mode the {@code textField} turns into {@code textLabel}
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
     * The onAction method for when the {@code deleteButton} is clicked
     * Removes the component from the underlying list and updates the
     * server using the {@code onChange} runnable.
     */
    @FXML
    private void onDeleteClicked() {
        if (parentList != null) {
            parentList.remove(listIndex);
        }
        if (onChange != null) {
            onChange.run();
        }
    }

    /**
     * Turns {@code textLabel} into {@code textField} and set {@code editButton}
     *  into a check character instead of edit character.
     */
    private void startEditing() {
        editing = true;
        textField.setText(textLabel.getText());

        textLabel.setVisible(false);
        textLabel.setManaged(false);

        deleteButton.setVisible(false);
        deleteButton.setManaged(false);

        textField.setVisible(true);
        textField.setManaged(true);

        textField.requestFocus();
        textField.selectAll();

        editButton.setText("✔");
        editButton.setTextFill(Color.GREEN);
    }

    /**
     * Turns {@code textField} into {@code textLabel} and set {@code editButton}
     * into an edit character instead of check character.
     * Checks whether the new item contains text otherwise the new item is removed
     * */
    private void finishEditing() {
        // 1. Get the current text from the input field
        String newText = textField.getText();

        // 2. Validation for NEW items: If empty, remove the item (cancel creation)
        if (newItem && (newText == null || newText.isBlank())) {
            editing = false; // Stop editing state
            if (parentList != null && listIndex >= 0 && listIndex < parentList.size()) {
                parentList.remove(listIndex);
            }
            if (onChange != null) onChange.run();
            return;
        }

        // 3. Validation for EXISTING items: If empty, BLOCK the save
        if (newText == null || newText.isBlank()) {
            // Apply red border to indicate error
            textField.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
            // Return immediately to keep the user in edit mode (editing remains true)
            return;
        }

        // 4. Valid Input: Proceed to save
        editing = false; // Now we can safely exit edit mode
        textField.setStyle(""); // Reset any error styles

        labelText = newText; // Update the internal field

        if (parentList != null && listIndex >= 0 && listIndex < parentList.size()) {
            parentList.set(listIndex, labelText);
        }

        textLabel.setText(labelText);

        textField.setVisible(false);
        textField.setManaged(false);

        deleteButton.setVisible(true);
        deleteButton.setManaged(true);

        textLabel.setVisible(true);
        textLabel.setManaged(true);

        editButton.setText("✏");
        editButton.setTextFill(Color.web("#2f06ff"));

        if (onChange != null) onChange.run();
    }
    /**
     * Initiate editing mode from parent controller class
     */
    public void startEditingFromCtrl() {
        if (!editing) {
            startEditing();
        }
    }
}
