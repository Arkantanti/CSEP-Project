package server.api;

import commons.Ingredient;
import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.IngredientRepository;
import server.database.IngredientRepositoryTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IngredientControllerTest {
    private IngredientController controller;
    private IngredientRepository repo;

    private Ingredient i1;
    private Ingredient i2;
    private Ingredient i3;

    @BeforeEach
    void setUp() {
        repo = new IngredientRepositoryTest();
        controller = new IngredientController(repo);

        i1 = new Ingredient("Bread", 5.3, 5.4, 1, Set.of());
        i2 = new Ingredient("Cheese", 5.3, 5.4, 2, Set.of());
        i3 = new Ingredient("Butter", 5.3, 5.4, 3, Set.of());

        repo.save(i1);
        repo.save(i2);
        repo.save(i3);

    }
    @Test
    void getById_validId() {
        ResponseEntity<Ingredient> response = controller.getById(i1.getId());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(i1, response.getBody());
    }

    @Test
    void getById_invalidId() {
        ResponseEntity<Ingredient> response = controller.getById(-999);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void getById_notfoundId() {
        ResponseEntity<Ingredient> response = controller.getById(999);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void add_validIngredient() {
        Ingredient i4 = new Ingredient("Milk", 5.3, 5.4, 1, Set.of());
        ResponseEntity<Ingredient> response = controller.add(i4);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(i4, response.getBody());
    }

    @Test
    void add_invalidIngredient() {
        Ingredient i4 = new Ingredient("", -5.3, 5.4, 1, Set.of());
        ResponseEntity<Ingredient> response = controller.add(i4);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void update_validIngredient() {
        Ingredient updated = new Ingredient("Curry", 5.3, 5.4, 10, Set.of());
        ResponseEntity<Ingredient> response = controller.update(i1.getId(), updated);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(updated, repo.findById(i1.getId()).orElse(null));
    }

    @Test
    void update_invalidId() {
        Ingredient updated = new Ingredient("Curry", 5.3, 5.4, 10, Set.of());
        ResponseEntity<Ingredient> response = controller.update(-1, updated);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void update_notfoundId() {
        Ingredient updated = new Ingredient("Curry", 5.3, 5.4, 10, Set.of());
        ResponseEntity<Ingredient> response = controller.update(999, updated);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void update_invalidIngredient() {
        Ingredient updated = new Ingredient("", 5.3, 5.4, 10, Set.of());
        ResponseEntity<Ingredient> response = controller.update(i1.getId(), updated);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void delete_validId() {
        ResponseEntity<Void> response = controller.delete(i1.getId());
        assertEquals(204, response.getStatusCode().value());
        assertFalse(repo.existsById(i1.getId()));
    }

    @Test
    void delete_invalidId() {
        ResponseEntity<Void> response = controller.delete(-10);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(repo.existsById(i1.getId()));
    }

    @Test
    void delete_notfoundId() {
        ResponseEntity<Void> response = controller.delete(999);
        assertEquals(404, response.getStatusCode().value());
        assertTrue(repo.existsById(i1.getId()));
    }
}