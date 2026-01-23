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

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import java.util.ResourceBundle;

import com.google.inject.Injector;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Utility class for loading FXML files with dependency injection support.
 */
public class MyFXML {

    private Injector injector;

    /**
     * Constructs a new MyFXML loader.
     *
     * @param injector the Guice injector used to create controller instances
     */
    public MyFXML(Injector injector) {
        this.injector = injector;
    }

    /**
     * Loads an FXML file and returns the controller and the root node.
     *
     * @param <T>   the type of the controller
     * @param c     the class of the controller
     * @param parts the path components to the FXML file
     * @return a Pair containing the controller instance and the root Parent node
     * @throws RuntimeException if the FXML file cannot be loaded
     */
    public <T> Pair<T, Parent> load(Class<T> c, String... parts) {
        try {
            var loader = new FXMLLoader(getLocation(parts), null, null,
                    new MyFactory(), StandardCharsets.UTF_8);
            Parent parent = loader.load();
            T ctrl = loader.getController();
            return new Pair<>(ctrl, parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads an FXML file and returns the controller and the root node.
     *
     * @param <T>   the type of the controller
     * @param c     the class of the controller
     * @param resources specifies files for a chosen language
     * @param parts the path components to the FXML file
     * @return a Pair containing the controller instance and the root Parent node
     * @throws RuntimeException if the FXML file cannot be loaded
     */
    public <T> Pair<T, Parent> load(Class<T> c, ResourceBundle resources, String... parts) {
        try {
            var loader = new FXMLLoader(getLocation(parts), resources, null,
                    new MyFactory(), StandardCharsets.UTF_8);
            Parent parent = loader.load();
            T ctrl = loader.getController();
            return new Pair<>(ctrl, parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resolves the URL of the FXML file based on the provided path parts.
     *
     * @param parts the path components (e.g., "client", "scenes", "Main.fxml")
     * @return the URL of the FXML resource
     */
    private URL getLocation(String... parts) {
        var path = Path.of("", parts).toString();
        return MyFXML.class.getClassLoader().getResource(path);
    }

    /**
     * Custom factory for creating controllers via the Injector.
     */
    private class MyFactory implements BuilderFactory, Callback<Class<?>, Object> {

        /**
         * Returns a builder for the specified type.
         *
         * @param type the class type to build
         * @return a Builder that retrieves the instance from the injector
         */
        @Override
        @SuppressWarnings("rawtypes")
        public Builder<?> getBuilder(Class<?> type) {
            return new Builder() {
                @Override
                public Object build() {
                    return injector.getInstance(type);
                }
            };
        }

        /**
         * Creates a controller instance using the injector.
         *
         * @param type the class of the controller to create
         * @return the instance of the controller
         */
        @Override
        public Object call(Class<?> type) {
            return injector.getInstance(type);
        }
    }
}