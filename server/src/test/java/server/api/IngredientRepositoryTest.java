package server.api;

import commons.Ingredient;
import server.database.IngredientRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IngredientRepositoryTest implements IngredientRepository {

    public final List<Ingredient> ingredients = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<Ingredient> findAllByOrderByNameAsc() {
        call("findAllByOrderByNameAsc");
        return ingredients.stream()
                .sorted(Comparator.comparing(Ingredient::getName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ingredient> findAll() {
        call("findAll");
        return ingredients;
    }

    @Override
    public <S extends Ingredient> S save(S entity) {
        call("save");
        ingredients.add(entity);
        return entity;
    }

    @Override
    public Optional<Ingredient> findById(Long id) {
        call("findById");
        return ingredients.stream().filter(i -> i.getId() == id).findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return findById(id).isPresent();
    }

    @Override
    public long count() {
        return ingredients.size();
    }

    @Override
    public void deleteById(Long id) {
        call("deleteById");
        ingredients.removeIf(i -> i.getId() == id);
    }

    // --- Boilerplate ---
    @Override public List<Ingredient> findAll(Sort sort) { return null; }
    @Override public List<Ingredient> findAllById(Iterable<Long> ids) { return null; }
    @Override public <S extends Ingredient> List<S> saveAll(Iterable<S> entities) { return null; }
    @Override public void flush() {}
    @Override public <S extends Ingredient> S saveAndFlush(S entity) { return null; }
    @Override public <S extends Ingredient> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
    @Override public void deleteAllInBatch(Iterable<Ingredient> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
    @Override public void deleteAllInBatch() {}
    @Override public Ingredient getOne(Long id) { return null; }
    @Override public Ingredient getById(Long id) { return findById(id).orElse(null); }
    @Override public Ingredient getReferenceById(Long id) { return getById(id); }
    @Override public <S extends Ingredient> List<S> findAll(Example<S> example) { return null; }
    @Override public <S extends Ingredient> List<S> findAll(Example<S> example, Sort sort) { return null; }
    @Override public Page<Ingredient> findAll(Pageable pageable) { return null; }
    @Override public void delete(Ingredient entity) { ingredients.remove(entity); }
    @Override public void deleteAllById(Iterable<? extends Long> ids) {}
    @Override public void deleteAll(Iterable<? extends Ingredient> entities) {}
    @Override public void deleteAll() { ingredients.clear(); }
    @Override public <S extends Ingredient> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override public <S extends Ingredient> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
    @Override public <S extends Ingredient> long count(Example<S> example) { return 0; }
    @Override public <S extends Ingredient> boolean exists(Example<S> example) { return false; }
    @Override public <S extends Ingredient, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) { return null; }
}