package client.config;

import commons.RecipeIngredient;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;


import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Config {

    private String serverUrl = "http://localhost:8080/"; //Default value

    private List<RecipeIngredient> shoppingList;

    private List<Long> favoriteRecipesIds = new ArrayList<>();

    private Path configPath;

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

    /**
     * Gets the list of favorite recipes ids.
     *
     * @return the list of favorite recipes ids
     */
    public List<Long> getFavoriteRecipesIds() {
        return favoriteRecipesIds;
    }

    /**
     * Sets the list of favorite recipes ids.
     *
     * @param favoriteRecipesIds the list of favorite recipes ids
     */
    public void setFavoriteRecipesIds(List<Long> favoriteRecipesIds) {
        this.favoriteRecipesIds = favoriteRecipesIds;
    }

    /**
     * Returns the path to the config file.
     *
     * @return the path to the config file
     */
    public Path getConfigPath() {
        return configPath;
    }

    /**
     * Sets the path to the config file.
     *
     * @param configPath the path to the config file
     */
    public void setConfigPath(Path configPath) {
        this.configPath = configPath;
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
