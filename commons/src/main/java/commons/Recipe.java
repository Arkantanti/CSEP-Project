package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;


@Entity
public class Recipe implements Showable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonIgnore
    @OneToMany(mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients;

    private String name;
    private int servings;
    private boolean cheap;
    private boolean fast;
    private boolean vegan;

    @ElementCollection
    private List<String> preparationSteps;

    /**
     * No argument constructor used by JPA for Recipe instantiation.
     * should not be used directly in code.
     */
    protected Recipe() {
        // for object mapper
    }

    /**
     * Creates a new Recipe with using name, number of servings and
     * preparation steps.
     *
     * @param name             the name of the recipe
     * @param servings         the number of servings this recipe provides
     * @param preparationSteps the list of ordered preparation instructions
     * @param cheap            whether this recipe is cheap
     * @param fast             whether this recipe is fast
     * @param vegan            whether this recipe is vegan
     */
    public Recipe(String name, int servings, List<String> preparationSteps,
                  boolean cheap, boolean fast, boolean vegan) {
        this.name = name;
        this.servings = servings;
        this.preparationSteps = preparationSteps==null ? null : new ArrayList<>(preparationSteps);
        this.cheap = cheap;
        this.fast = fast;
        this.vegan = vegan;
    }


    public boolean isCheap() { return cheap; }

    public boolean isFast() { return fast; }

    public boolean isVegan() { return vegan; }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getServings() {
        return servings;
    }

    public List<String> getPreparationSteps() {
        return preparationSteps==null ? null : new ArrayList<>(preparationSteps);
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients==null ? null : new ArrayList<>(recipeIngredients);}

    public void setName(String name){ this.name = name;}

    public void setId(Long id){ this.id = id;}

    public void setServings(int servings){ this.servings = servings; }

    public void setPreparationSteps(List<String> preparationSteps) {
        this.preparationSteps = preparationSteps==null ? null : new ArrayList<>(preparationSteps);
    }
    public void setCheap(boolean cheap) { this.cheap = cheap; }
    public void setFast(boolean fast) { this.fast = fast; }
    public void setVegan(boolean vegan) { this.vegan = vegan; }

    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients==null ? null : new ArrayList<>(recipeIngredients);
    }

    /**
     * Adds a preparation step to {@code preparationSteps} steps of a recipe
     * @param preparationStep the step to add
     * @throws IllegalArgumentException if {@code preparationSteps} is null or
     *  if {@code preparationStep} is null or blank
     */
    public void addPreparationStep(String preparationStep) {
        if (preparationSteps == null) {
            throw new IllegalArgumentException("Preparation steps array is not initialised");
        }
        if (preparationStep == null || preparationStep.isBlank()) {
            throw new IllegalArgumentException("Preparation step must not be null or blank");
        }
        preparationSteps.add(preparationStep);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
