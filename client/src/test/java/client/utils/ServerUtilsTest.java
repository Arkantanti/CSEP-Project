package client.utils;

import client.config.Config;
import commons.Recipe;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerUtilsTest {

    @Mock
    private Client client;

    @Mock
    private WebTarget target;

    @Mock
    private Invocation.Builder builder;

    @Mock
    private Response response;

    @Mock
    private Config config;

    private ServerUtils sut;

    @BeforeEach
    public void setup() {
        lenient().when(config.getServerUrl()).thenReturn("http://localhost:8080/");
        lenient().when(client.target(anyString())).thenReturn(target);
        lenient().when(target.path(anyString())).thenReturn(target);
        lenient().when(target.request(anyString())).thenReturn(builder);

        sut = new ServerUtils(config, client);
    }

    @Test
    void getRecipes_success() {
        Recipe recipe = new Recipe("Pancakes", 1, List.of("Mix", "Fry"));
        List<Recipe> expected = List.of(recipe);

        when(builder.get(ArgumentMatchers.<GenericType<List<Recipe>>>any()))
                .thenReturn(expected);

        List<Recipe> result = sut.getRecipes();

        assertEquals(expected, result);
        verify(target).path("api/recipes/");
    }

    @Test
    void getRecipeById_success() {
        Recipe expected = new Recipe("Test Recipe", 2, List.of());
        expected.setId(123L);

        // Mock the chain of calls: client -> target -> path -> request -> get -> response
        when(target.path("api/recipes/123")).thenReturn(target);
        when(builder.get()).thenReturn(response);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(Recipe.class)).thenReturn(expected);

        Recipe result = sut.getRecipeById(123L);

        assertNotNull(result);
        assertEquals(123L, result.getId());
        assertEquals("Test Recipe", result.getName());
    }

    @Test
    void getRecipeById_notFound() {
        when(target.path("api/recipes/999")).thenReturn(target);
        when(builder.get()).thenReturn(response);
        when(response.getStatus()).thenReturn(404);

        Recipe result = sut.getRecipeById(999L);

        assertNull(result);
    }

    @Test
    void searchRecipes_withQuery() {
        when(target.queryParam(anyString(), any())).thenReturn(target);
        when(builder.get(ArgumentMatchers.<GenericType<List<Recipe>>>any()))
                .thenReturn(Collections.emptyList());

        sut.searchRecipes("soup");

        verify(target).queryParam("name", "soup");
        verify(target).path("api/recipes/search");
    }

    @Test
    void addRecipe_success() {
        Recipe input = new Recipe("New", 0, List.of());

        // FIX: Explicitly set the ID on the "saved" recipe so expected value matches
        Recipe saved = new Recipe("New", 0, List.of());
        saved.setId(42L);

        when(builder.post(any(Entity.class), eq(Recipe.class))).thenReturn(saved);

        Recipe result = sut.add(input);

        assertEquals(42, result.getId());
        verify(target).path("/api/recipes/");
    }

    @Test
    void updateRecipe_success() {
        Recipe update = new Recipe("Updated", 10, List.of());
        // FIX: Explicitly set the ID so the URL path is correct ("api/recipes/10")
        update.setId(10L);

        when(builder.put(any(Entity.class), eq(Recipe.class))).thenReturn(update);

        Recipe result = sut.updateRecipe(update);

        assertNotNull(result);
        verify(target).path("api/recipes/10");
    }

    @Test
    void updateRecipe_invalidInput() {
        assertThrows(IllegalArgumentException.class, () -> sut.updateRecipe(null));

        Recipe invalid = new Recipe("Bad", 10, List.of());
        // FIX: Explicitly set an invalid ID (< 0) to trigger the exception
        invalid.setId(-1L);

        assertThrows(IllegalArgumentException.class, () -> sut.updateRecipe(invalid));
    }

    @Test
    void isServerAvailable_true() {
        when(builder.get()).thenReturn(response);
        assertTrue(sut.isServerAvailable());
    }

    @Test
    void isServerAvailable_false() {
        when(builder.get()).thenThrow(new ProcessingException("Down"));
        assertFalse(sut.isServerAvailable());
    }

    @Test
    void deleteRecipeIngredient_success() {
        when(builder.delete()).thenReturn(response);

        Response r = sut.deleteRecipeIngredient(5);

        assertEquals(response, r);
        verify(target).path("api/recipeingredients/5");
    }

    @Test
    void deleteRecipeIngredient_invalidId() {
        assertThrows(IllegalArgumentException.class, () -> sut.deleteRecipeIngredient(-1));
    }
}