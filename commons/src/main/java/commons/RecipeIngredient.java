package commons;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(optional = false)
    private Recipe recipe;

    @ManyToOne(optional = false)
    private Ingredient ingredient;

    private String informalUnit; // e.g. pinch, tablespoon
    private double amount; // numerical value; 5g salt, 0.5 litres milk, etc

    @Enumerated(EnumType.STRING)
    private Unit unit;

    /**
     * No argument constructor used by JPA for RecipeIngredient object instantiation.
     * should not be used directly in code.
     */
    protected RecipeIngredient() {
        // for object mapping
    }

    /**
     * Creates a new RecipeIngredient object that links a Recipe to a
     * Ingredient, with either a structured amount and unit or optional
     * informal unit.
     * @param recipe       the recipe this ingredient is part of
     * @param ingredient   the ingredient used in the recipe
     * @param informalUnit a String description of the informal amount (e.g. "a pinch",
     *                     "1 clove"). Optional attribute, so may be null
     * @param amount       the numeric amount of the ingredient
     * @param unit         the unit of the amount
     */
    public RecipeIngredient(Recipe recipe,
                            Ingredient ingredient,
                            String informalUnit,
                            double amount,
                            Unit unit) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.informalUnit = informalUnit;
        this.amount = amount;
        this.unit = unit;
    }

    public long getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public String getInformalUnit() {
        return informalUnit;
    }

    public double getAmount() {
        return amount;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setIngredient(Ingredient ingredient){ this.ingredient = ingredient; }

    public void setInformalUnit(String informalUnit){ this.informalUnit = informalUnit; }

    public void setAmount(double amount){ this.amount = amount; }

    public void setUnit(Unit unit){ this.unit = unit; }

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
