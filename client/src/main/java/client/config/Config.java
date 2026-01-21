package client.config;

import client.model.ShoppingListItem;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Config {

    private String serverUrl = "http://localhost:8080/"; //Default value

    private List<ShoppingListItem> shoppingList;

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

    public List<ShoppingListItem> getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(List<ShoppingListItem> shoppingList) {
        this.shoppingList = shoppingList;
    }

    private boolean engLanguage;
    private boolean polLanguage;
    private boolean dutLanguage;

    /**
     * Gets the list of favorite recipes ids.
     *
     * @return the list of favorite recipes ids
     */
    public List<Long> getFavoriteRecipesIds() {
        return favoriteRecipesIds;
    }

    public boolean isDutLanguage() {
        return dutLanguage;
    }

    public boolean isEngLanguage() {
        return engLanguage;
    }

    public boolean isPolLanguage() {
        return polLanguage;
    }

    public void setDutLanguage(boolean dutLanguage) {
        this.dutLanguage = dutLanguage;
    }

    public void setEngLanguage(boolean engLanguage) {
        this.engLanguage = engLanguage;
    }

    public void setPolLanguage(boolean polLanguage) {
        this.polLanguage = polLanguage;
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
