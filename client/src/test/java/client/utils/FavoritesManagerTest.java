package client.utils;

import client.config.Config;
import commons.Recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FavoritesManagerTest {

    private Config config;
    private FavoritesManager manager;

    @BeforeEach
    void setup() {
        config = mock(Config.class);
        when(config.getFavoriteRecipesIds()).thenReturn(new ArrayList<>());
        manager = new FavoritesManager(config, null);
    }

    @Test
    void isFavorite_returnsTrueWhenRecipeIsFavorited() {
        when(config.getFavoriteRecipesIds()).thenReturn(new ArrayList<>(List.of(1L, 2L, 3L)));

        assertTrue(manager.isFavorite(1L));
    }

    @Test
    void isFavorite_returnsTrueMultipleFavorites() {
        List<Long> favorites = new ArrayList<>();
        favorites.add(5L);
        favorites.add(10L);
        favorites.add(15L);
        when(config.getFavoriteRecipesIds()).thenReturn(favorites);

        assertTrue(manager.isFavorite(5L));
        assertTrue(manager.isFavorite(10L));
        assertTrue(manager.isFavorite(15L));
    }

    @Test
    void isFavorite_returnsFalseWhenRecipeIsNotFavorited() {
        when(config.getFavoriteRecipesIds()).thenReturn(new ArrayList<>(List.of(1L, 2L)));

        assertFalse(manager.isFavorite(3L));
    }

    @Test
    void isFavorite_returnsFalseWhenDifferentId() {
        ArrayList<Long> favorites = new ArrayList<>();
        favorites.add(1L);
        favorites.add(2L);
        favorites.add(3L);

        when(config.getFavoriteRecipesIds()).thenReturn(favorites);

        boolean result = manager.isFavorite(4L);
        assertEquals(false, manager.isFavorite(4L));
    }

    @Test
    void isFavorite_returnsFalseWhenFavoritesListIsEmpty() {
        when(config.getFavoriteRecipesIds()).thenReturn(new ArrayList<>());

        assertFalse(manager.isFavorite(1L));
        assertFalse(manager.isFavorite(2L));
    }

    @Test
    void validate_throwsWhenFavoritesListIsNull() {
        when(config.getFavoriteRecipesIds()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> manager.validate());
    }

    @Test
    void validate_returnsEmptyListWhenFavoritesIsEmpty() throws Exception {
        when(config.getFavoriteRecipesIds()).thenReturn(new ArrayList<>());

        List<Long> removed = manager.validate();

        assertEquals(0, removed.size());
    }
}
