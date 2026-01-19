package client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import commons.Unit;
import commons.RecipeIngredient;

import java.util.Dictionary;
import java.util.Hashtable;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

/**
 * Represents an item in the shopping list.
 * Can be either an ingredient-based item (with amount, unit, and optional recipe name)
 * or a simple text item (for unrelated items like "a box of cereal").
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoppingListItem {

    private Long ingredientId; // null for text-only items
    private String ingredientName; // name of the ingredient, null for text-only items
    private String text; // for text-only items, null for ingredient-based items
    private String informalUnit; // e.g. pinch, tablespoon
    private double amount; // numerical amount values, e.g. 5g salt, 1.0 litres milk etc
    private Unit unit;
    private String recipeName; // name of the recipe this ingredient comes from.

    /**
     * No argument constructor used by Jackson for object instantiation.
     * should not be used directly in code.
     */
    protected ShoppingListItem() {
        // for object mapping
    }

    /**
     * Creates a new ShoppingListItem for an ingredient-based item.
     *
     * @param ingredientId   the ID of the ingredient
     * @param ingredientName the name of the ingredient
     * @param informalUnit   a String description of the informal amount (e.g. "a pinch",
     *                       "1 clove"). May be null.
     * @param amount         the numeric amount of the ingredient
     * @param unit           the unit of the amount
     * @param recipeName     the name of the recipe this ingredient comes from. Null for text-based items, may be null for ingredient-based items.
     */
    public ShoppingListItem(Long ingredientId,
                            String ingredientName,
                            String informalUnit,
                            double amount,
                            Unit unit,
                            String recipeName) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.informalUnit = informalUnit;
        this.amount = amount;
        this.unit = unit;
        this.recipeName = (recipeName != null && !recipeName.isBlank()) ? recipeName : null;
    }

    /**
     * Creates a ShoppingListItem from a recipeIngredient
     * @param recipeIngredient the RecipeIngredient to base this ShoppingListItem off of
     * @param scalar a multiplier for the recipeIngredient
     */
    public ShoppingListItem(RecipeIngredient recipeIngredient, double scalar) {
        this.ingredientId = recipeIngredient.getIngredient().getId();
        this.ingredientName = recipeIngredient.getIngredient().getName();
        this.informalUnit = recipeIngredient.getInformalUnit();
        this.amount = recipeIngredient.getAmount() * scalar;
        this.unit = recipeIngredient.getUnit();
        this.recipeName = recipeIngredient.getRecipe().getName();
    }

    /**
     * Creates a new ShoppingListItem for a text-only item (unrelated item).
     *
     * @param text the text description of the item (e.g. a box of cereal)
     */
    public ShoppingListItem(String text) {
        this.text = text;
        this.ingredientId = null;
        this.ingredientName = null;
        this.recipeName = null;
    }

    /**
     * Checks if this is a text-only item (not ingredient-based).
     *
     * @return true if this is a text-only item, false if it's ingredient-based
     */
    public boolean isTextOnly() {
        return text != null;
    }

    /**
     * Formats this item for display.
     *
     * @return a formatted string representation of this shopping list item, e.g. 100g salt (from: Recipe 1)
     */
    public String formatItem() {
        if (isTextOnly()) {
            return text;
        }

        StringBuilder s = new StringBuilder();
        s.append(formatAmount());
        s.append(" ").append(ingredientName);

        if (recipeName != null && !recipeName.isBlank()) {
            s.append(" ( ").append(recipeName).append(")");
        }

        return s.toString();
    }

    /**
     * Formats the amount part of an ingredient-based item.
     *
     * @return a formatted string for the amount and unit
     */
    private String formatAmount() {
        if (unit == Unit.CUSTOM) {
            return informalUnit != null ? informalUnit : String.valueOf(amount);
        }

        // List politely yoinked from https://en.wikipedia.org/wiki/Metric_prefix
        Dictionary<Integer, String> metricPrefixes = new Hashtable<>() {
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
                put(0, "");
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

        String unitChar = switch (unit) {
            case GRAM -> "g";
            case LITER -> "L";
            case CUSTOM -> "";
        };

        return (amount / Math.pow(10, magnitude)) + " "
                + metricPrefixes.get(magnitude) + unitChar;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName(){
        return this.ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getInformalUnit() {
        return informalUnit;
    }

    public void setInformalUnit(String informalUnit) {
        this.informalUnit = informalUnit;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeName() {
        return recipeName;
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
