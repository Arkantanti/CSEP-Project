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
package server.api;

import java.util.List;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import commons.Quote;
import server.database.QuoteRepository;

/**
 * REST Controller for managing quotes in the database.
 */
@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final Random random;
    private final QuoteRepository repo;

    /**
     * Constructs a new QuoteController.
     *
     * @param random the random number generator used for selecting random quotes
     * @param repo   the repository for accessing quote data
     */
    public QuoteController(Random random, QuoteRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    /**
     * Retrieves all available quotes.
     *
     * @return a list of all quotes in the database
     */
    @GetMapping(path = { "", "/" })
    public List<Quote> getAll() {
        return repo.findAll();
    }

    /**
     * Retrieves a specific quote by its unique identifier.
     *
     * @param id the ID of the quote to retrieve
     * @return the quote if found, or a Bad Request response if invalid or not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Quote> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Adds a new quote to the database.
     * Validates that the person and quote text are not null or empty.
     *
     * @param quote the quote object to add
     * @return the saved quote, or a Bad Request response if validation fails
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Quote> add(@RequestBody Quote quote) {

        if (quote.getPerson() == null
                || isNullOrEmpty(quote.getPerson().getFirstName())
                || isNullOrEmpty(quote.getPerson().getLastName())
                || isNullOrEmpty(quote.getQuote())) {
            return ResponseEntity.badRequest().build();
        }

        Quote saved = repo.save(quote);
        return ResponseEntity.ok(saved);
    }

    /**
     * Helper method to check if a string is null or empty.
     *
     * @param s the string to check
     * @return true if the string is null or has a length of 0
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Retrieves a random quote from the database.
     *
     * @return a random Quote object
     */
    @GetMapping("rnd")
    public ResponseEntity<Quote> getRandom() {
        var quotes = repo.findAll();
        var idx = random.nextInt((int) repo.count());
        return ResponseEntity.ok(quotes.get(idx));
    }
}