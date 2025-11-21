/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Person;
import commons.Quote;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

public class AddQuoteCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField quote;

    /**
     * Constructor for the AddQuoteCtrl.
     *
     * @param server   the server utility used for network communication
     * @param mainCtrl the main controller used for navigation
     */
    @Inject
    public AddQuoteCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Cancels the current action, clears the form, and returns to the overview.
     */
    public void cancel() {
        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * Persists the new quote to the server and returns to the overview.
     * Shows an error alert if the server communication fails.
     */
    public void ok() {
        try {
            server.addQuote(getQuote());
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * Helper method to construct a Quote object from the text fields.
     *
     * @return a new Quote object containing the data from the form
     */
    private Quote getQuote() {
        var p = new Person(firstName.getText(), lastName.getText());
        var q = quote.getText();
        return new Quote(p, q);
    }

    /**
     * Clears the text content of all input fields in the form.
     */
    private void clearFields() {
        firstName.clear();
        lastName.clear();
        quote.clear();
    }

    /**
     * Handles key presses for global shortcuts (Enter to submit, Escape to cancel).
     *
     * @param e the KeyEvent triggered by the user
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                ok();
                break;
            case ESCAPE:
                cancel();
                break;
            default:
                break;
        }
    }
}