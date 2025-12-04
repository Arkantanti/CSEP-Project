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

    public void setId(Long id){ this.id = id;}

    public void setServings(int servings){ this.servings = servings; }

    public void setPreparationSteps(List<String> preparationSteps) {
        this.preparationSteps = preparationSteps;
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
