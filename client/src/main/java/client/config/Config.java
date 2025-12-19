package client.config;

import commons.RecipeIngredient;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Config {

    private String serverUrl = "http://localhost:8080/"; //Default value

    private List<RecipeIngredient> shoppingList;

    /**
     * No argument constructor used by Jackson.
     */
    protected Config() {
        // for object mapper
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public List<RecipeIngredient> getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(List<RecipeIngredient> shoppingList) {
        this.shoppingList = shoppingList;
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
