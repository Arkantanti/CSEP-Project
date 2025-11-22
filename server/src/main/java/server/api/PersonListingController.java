/**
 * Copyright 2024 Sebastian Proksch
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package server.api;

import java.util.LinkedList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import commons.Person;

/**
 * REST Controller for managing a list of people.
 * Provides endpoints to retrieve and add persons.
 */
@RestController
@RequestMapping("/api/people")
public class PersonListingController {

    private final List<Person> people = new LinkedList<>();

    /**
     * Constructs the controller and initializes it with some default data.
     */
    public PersonListingController() {
        people.add(new Person("Mickey", "Mouse"));
        people.add(new Person("Donald", "Duck"));
    }

    /**
     * Retrieves the list of all people stored in the memory.
     *
     * @return a list containing all Person objects
     */
    @GetMapping("/")
    public List<Person> list() {
        return people;
    }

    /**
     * Adds a new person to the list if they are not already present.
     *
     * @param p the person object to add
     * @return the updated list of all people
     */
    @PostMapping("/")
    public List<Person> add(@RequestBody Person p) {
        if (!people.contains(p)) {
            people.add(p);
        }
        return people;
    }
}