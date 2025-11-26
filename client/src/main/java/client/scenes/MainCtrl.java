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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * The Main Controller that manages the execution flow and scene switching.
 */
public class MainCtrl {

    private Stage primaryStage;

    private RecipeOverviewCtrl overviewCtrl;
    private Scene overview;

    /**
     * Initializes the main controller with the primary stage and the necessary scenes.
     *
     * @param primaryStage the primary stage of the application
     * @param overview     the pair containing the controller and parent for the overview scene
     */
    public void initialize(Stage primaryStage, Pair<RecipeOverviewCtrl, Parent> overview) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());


        showOverview();
        primaryStage.show();
    }

    /**
     * Displays the recipe overview scene.
     * Sets the title and refreshes the data in the overview controller.
     */
    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
//        overviewCtrl.refresh();
    }
}