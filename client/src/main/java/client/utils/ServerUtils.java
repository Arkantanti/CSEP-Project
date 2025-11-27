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
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import client.config.Config;
import com.google.inject.Inject;
import commons.Recipe; // <--- DON'T FORGET THIS IMPORT
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

/**
 * Utility class for communicating with the server via REST.
 */
public class ServerUtils {

    private final String serverURL;

    /**
     * Constructor for the ServerUtils class.
     * @param cfg Client config object. Injected with Guice.
     */
    @Inject
    public ServerUtils(Config cfg) {
        serverURL = cfg.getServerUrl();
    }

    /**
     * Retrieves a list of all recipes from the server.
     *
     * @return a list of Recipe objects
     */
    public List<Recipe> getRecipes() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverURL).path("api/recipes/")
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Recipe>>() {
                });
    }

    // ... (Keep the Quote methods if you still need them, or delete them) ...

    /**
     * Checks if the server is currently reachable.
     *
     * @return true if the server is reachable, false otherwise
     */
    public boolean isServerAvailable() {
        try {
            ClientBuilder.newClient(new ClientConfig())
                    .target(serverURL)
                    .request(APPLICATION_JSON)
                    .get();
        } catch (ProcessingException e) {
            return false;
        }
        return true;
    }
}