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
public class Ingredient implements Showable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonIgnore
    @OneToMany(mappedBy = "ingredient",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients;

    private String name; // ingredient name

    // per 100g
    private double fat;
    private double protein;
    private double carbs;

    /**
     * Creates a new Ingredient object with the given name and nutritional values
     * per 100 grams.
     *
     * @param name    the name of the ingredient
     * @param fat     the amount of fat per 100 grams of the ingredient
     * @param protein the amount of protein per 100 grams of the ingredient
     * @param carbs   the amount of carbohydrates per 100 grams of the ingredient
     */
    public Ingredient(String name,
                      double fat,
                      double protein,
                      double carbs) {
        this.name = name;
        this.fat = fat;
        this.protein = protein;
        this.carbs = carbs;
    }

    /**
     * No argument constructor used by JPA for Ingredient instantiation.
     * should not be used directly in code.
     */
    protected Ingredient() {
        //for object mapping
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getFat() {
        return fat;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public List<RecipeIngredient> getRecipeIngredients() {return recipeIngredients;}

    public void setId(long id) { this.id = id;}

    public void setName(String name){
        this.name = name;
    }

    public void setFat(double fat){
        this.fat = fat;
    }

    public void setProtein(double protein){
        this.protein = protein;
    }

    public void setCarbs(double carbs){
        this.carbs = carbs;
    }

    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients){
        this.recipeIngredients = recipeIngredients;
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
