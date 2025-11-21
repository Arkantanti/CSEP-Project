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
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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

    private static final String SERVER = "http://localhost:8080/";

    /**
     * Retrieves quotes using standard Java IO streams instead of a REST client.
     * This is a demonstration of the "hard way" to fetch data.
     *
     * @throws IOException        if an I/O error occurs during the connection
     * @throws URISyntaxException if the URL string is not formatted correctly
     */
    public void getQuotesTheHardWay() throws IOException, URISyntaxException {
        var url = new URI("http://localhost:8080/api/quotes").toURL();
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * Retrieves a list of all quotes from the server using JAX-RS.
     *
     * @return a list of Quote objects
     */
    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/quotes")
                .request(APPLICATION_JSON)
                .get(new GenericType<List<Quote>>() {
                });
    }

    /**
     * Persists a new quote to the server.
     *
     * @param quote the Quote object to add
     * @return the saved Quote object returned by the server
     */
    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/quotes")
                .request(APPLICATION_JSON)
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }

    /**
     * Checks if the server is currently reachable.
     *
     * @return true if the server is reachable, false otherwise
     */
    public boolean isServerAvailable() {
        try {
            ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER)
                    .request(APPLICATION_JSON)
                    .get();
        } catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                return false;
            }
        }
        return true;
    }
}