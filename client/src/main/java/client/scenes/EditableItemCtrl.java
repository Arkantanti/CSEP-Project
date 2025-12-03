package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

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
     *  into an edit character instead of check character
     *  */
    private void finishEditing() {
        editing = false;

        labelText = textField.getText();
        textLabel.setText(labelText);

        textField.setVisible(false);
        textField.setManaged(false);

        deleteButton.setVisible(true);
        deleteButton.setManaged(true);

        textLabel.setVisible(true);
        textLabel.setManaged(true);

        editButton.setText("✏");
        editButton.setTextFill(Color.web("#2f06ff"));
    }
}
