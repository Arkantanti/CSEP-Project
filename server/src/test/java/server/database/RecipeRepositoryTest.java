package server.database;

import commons.Recipe;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RecipeRepositoryTest implements RecipeRepository {

    public final List<Recipe> recipes = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<Recipe> findByNameContainingIgnoreCase(String name) {
        call("findByNameContainingIgnoreCase");
        if (name == null) return new ArrayList<>();
        String lowerName = name.toLowerCase();
        return recipes.stream()
                .filter(r -> r.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Recipe> findAll() {
        call("findAll");
        return recipes;
    }

    @Override
    public <S extends Recipe> S save(S entity) {
        call("save");
        if(entity.getId()==0 ||
                recipes.stream().noneMatch(q -> q.getId() == entity.getId())) {
            entity.setId(recipes.stream().mapToLong(Recipe::getId).max().orElse(0) + 1);
        } else {
            recipes.removeIf(q -> q.getId() == entity.getId());
        }
        recipes.add(entity);
        return entity;
    }

    @Override
    public Optional<Recipe> findById(Long id) {
        call("findById");
        return recipes.stream().filter(q -> q.getId() == id).findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return findById(id).isPresent();
    }

    @Override
    public long count() {
        call("count");
        return recipes.size();
    }

    @Override
    public void deleteById(Long id) {
        call("deleteById");
        recipes.removeIf(r -> r.getId() == id);
    }

    // --- Boilerplate methods required by interface (return null/do nothing) ---

    @Override public List<Recipe> findAll(Sort sort) { return null; }
    @Override public List<Recipe> findAllById(Iterable<Long> ids) { return null; }
    @Override public <S extends Recipe> List<S> saveAll(Iterable<S> entities) { return null; }
    @Override public void flush() {}
    @Override public <S extends Recipe> S saveAndFlush(S entity) { return null; }
    @Override public <S extends Recipe> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
    @Override public void deleteAllInBatch(Iterable<Recipe> entities) {}
    @Override public void deleteAllByIdInBatch(Iterable<Long> ids) {}
    @Override public void deleteAllInBatch() {}
    @Override public Recipe getOne(Long id) { return null; }
    @Override public Recipe getById(Long id) { return findById(id).orElse(null); }
    @Override public Recipe getReferenceById(Long id) { return getById(id); }
    @Override public <S extends Recipe> List<S> findAll(Example<S> example) { return null; }
    @Override public <S extends Recipe> List<S> findAll(Example<S> example, Sort sort) { return null; }
    @Override public Page<Recipe> findAll(Pageable pageable) { return null; }
    @Override public void delete(Recipe entity) { recipes.remove(entity); }
    @Override public void deleteAllById(Iterable<? extends Long> ids) {}
    @Override public void deleteAll(Iterable<? extends Recipe> entities) {}
    @Override public void deleteAll() { recipes.clear(); }
    @Override public <S extends Recipe> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override public <S extends Recipe> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
    @Override public <S extends Recipe> long count(Example<S> example) { return 0; }
    @Override public <S extends Recipe> boolean exists(Example<S> example) { return false; }
    @Override public <S extends Recipe, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) { return null; }
}