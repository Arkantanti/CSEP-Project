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

    @FXML
    public void initialize() {
        // default state is label with text
        textField.setVisible(false);
        textField.setManaged(false);

        textField.setMinWidth(Region.USE_PREF_SIZE);
        textField.setMaxWidth(Region.USE_PREF_SIZE);

        // Set text if it was already set before initialize
        if (labelText != null) {
            textLabel.setText(labelText);
        }
    }

    public void setText(String text) {
        labelText = text;
        if (textLabel != null) {
            textLabel.setText(text);
        }
    }

    public String getText() {
        return labelText;
    }

    @FXML
    private void onEditClicked() {
        if (!editing) {
            startEditing();
        } else {
            finishEditing();
        }
    }

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
