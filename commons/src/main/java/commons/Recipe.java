package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Recipe implements Showable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private int servings;

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
     */
    public Recipe(String name,
                  int servings,
                  List<String> preparationSteps) {
        this.name = name;
        this.servings = servings;
        this.preparationSteps = preparationSteps;
    }

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
        return preparationSteps;
    }

    public void setName(String name){ this.name = name;}

    public void setServings(int servings){ this.servings = servings; }

    public void setPreparationSteps(List<String> preparationSteps) {
        this.preparationSteps = preparationSteps;
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
