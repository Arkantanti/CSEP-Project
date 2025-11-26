package server.api;

import commons.RecipeIngredient;
import server.database.RecipeIngredientRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class RecipeIngredientRepositoryTest implements RecipeIngredientRepository {

    public final List<RecipeIngredient> recipeIngredients = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<RecipeIngredient> findAll() {
        call("findAll");
        return recipeIngredients;
    }

    @Override
    public <S extends RecipeIngredient> S save(S entity) {
        call("save");
        recipeIngredients.add(entity);
        return entity;
    }

    @Override
    public Optional<RecipeIngredient> findById(Long id) {
        call("findById");
        return recipeIngredients.stream().filter(ri -> ri.getId() == id).findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return findById(id).isPresent();
    }

    @Override
    public long count() {
        return recipeIngredients.size();
    }

    @Override
    public void deleteById(Long id) {
        call("deleteById");
        recipeIngredients.removeIf(ri -> ri.getId() == id);
    }

    // --- Boilerplate ---
    @Override public List<RecipeIngredient> findAll(Sort sort) { return null; }
    @Override public List<RecipeIngredient> findAllById(Iterable<Long> ids) { return null; }
    @Override public <S extends RecipeIngredient> List<S> saveAll(Iterable<S> entities) { return null; }
    @Override public void flush() {}
    @Override public <S extends RecipeIngredient> S saveAndFlush(S entity) { return null; }
    @Override public <S extends RecipeIngredient> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
    @Override public void deleteAllInBatch(Iterable<RecipeIngredient> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
    @Override public void deleteAllInBatch() {}
    @Override public RecipeIngredient getOne(Long id) { return null; }
    @Override public RecipeIngredient getById(Long id) { return findById(id).orElse(null); }
    @Override public RecipeIngredient getReferenceById(Long id) { return getById(id); }
    @Override public <S extends RecipeIngredient> List<S> findAll(Example<S> example) { return null; }
    @Override public <S extends RecipeIngredient> List<S> findAll(Example<S> example, Sort sort) { return null; }
    @Override public Page<RecipeIngredient> findAll(Pageable pageable) { return null; }
    @Override public void delete(RecipeIngredient entity) { recipeIngredients.remove(entity); }
    @Override public void deleteAllById(Iterable<? extends Long> ids) {}
    @Override public void deleteAll(Iterable<? extends RecipeIngredient> entities) {}
    @Override public void deleteAll() { recipeIngredients.clear(); }
    @Override public <S extends RecipeIngredient> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override public <S extends RecipeIngredient> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
    @Override public <S extends RecipeIngredient> long count(Example<S> example) { return 0; }
    @Override public <S extends RecipeIngredient> boolean exists(Example<S> example) { return false; }
    @Override public <S extends RecipeIngredient, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) { return null; }
    @Override
    public List<RecipeIngredient> findByRecipeId(long recipeId) {
        return List.of();
    }
}