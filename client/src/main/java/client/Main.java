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
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import client.config.Config;
import client.config.ConfigManager;
import client.scenes.RecipeOverviewCtrl;
import com.google.inject.Injector;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for the client-side application.
 */
public class Main extends Application {

    private static Injector injector;
    private static MyFXML fxml;

    /**
     * Main method to launch the application.
     *
     * @param args command line arguments
     * @throws URISyntaxException if there is an error with URI syntax
     * @throws IOException        if an I/O error occurs
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        Path cfgPath = ConfigManager.getConfigPath(args);
        Config config = ConfigManager.loadOrCreate(cfgPath);

        MyModule.setConfig(config);

        injector = createInjector(new MyModule());
        fxml = new MyFXML(injector);
        launch();
    }

    /**
     * Starts the JavaFX application stage.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if an error occurs during startup
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        var serverUtils = injector.getInstance(ServerUtils.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "The server URL is incorrect or the server is unavailable. Shutting down.";
            System.err.println(msg);
            return;
        }

        var overview = fxml.load(RecipeOverviewCtrl.class,
                "client", "scenes", "RecipeOverview.fxml");

        var mainCtrl = injector.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, overview);
    }
}