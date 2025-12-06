package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Dictionary;
import java.util.Hashtable;

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
     * Ingredient, with either a formal amount and unit or an
     * informal amount.
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

    public void setId(long id) {this.id = id;}

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setIngredient(Ingredient ingredient){ this.ingredient = ingredient; }

    public void setInformalUnit(String informalUnit){ this.informalUnit = informalUnit; }

    public void setAmount(double amount){ this.amount = amount; }

    public void setUnit(Unit unit){ this.unit = unit; }

    /**
     *  helper function to format recipeIngredient
     * @return a formatted version of the toString. ex: 100 L salt
     */
    public String formatIngredient() {
        StringBuilder s = new StringBuilder();

        // List politely yoinked from https://en.wikipedia.org/wiki/Metric_prefix
        Dictionary<Integer, String> metricPrefixes = new Hashtable<Integer, String>(){
            {
                put(-30, "q"); // quecto
                put(-27, "r");
                put(-24, "y");
                put(-21, "z");
                put(-18, "a");
                put(-15, "f");
                put(-12, "p");
                put(-9, "n");
                put(-6, "μ");
                put(-3, "m");
//                put(-2, "c");
//                put(-1, "d");   // deci
                put(0, "");
//              put(1, "da"); centi,deci,Hecto and Deca are not that useful, so I'll comment them
//              put(2, "h");  However I really cant wait for my recipe using 1 quectogram of salt
                put(3, "k");    // kilo
                put(6, "M");    // mega
                put(9, "G");    // giga
                put(12, "T");
                put(15, "P");
                put(18, "E");
                put(21, "Z");
                put(24, "Y");
                put(27, "R");
                put(30, "Q"); //quetta
            }
        };

        int magnitude = (int) Math.floor(Math.log10(amount));
        if (magnitude < -30) magnitude = -30;
        if (magnitude > 30) magnitude = 30;

        while (metricPrefixes.get(magnitude) == null && magnitude > -30) {
            magnitude--;
        }

        String unitChar = switch (unit){
            case GRAM -> "g";
            case LITER -> "L";
            case CUSTOM -> "";
        };

        if (unit != Unit.CUSTOM) {
            s.append(amount / Math.pow(10, magnitude)).append(" ")
                    .append(metricPrefixes.get(magnitude)).append(unitChar);
        }
        else {
            if (informalUnit != null) {
                s.append(informalUnit);
            }
            else {
                s.append(amount);
            }
        }
        s.append(" ").append(ingredient.getName());
        return s.toString();
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
