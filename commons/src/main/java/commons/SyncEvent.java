package commons;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import commons.RecipeIngredient;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SyncEvent.RecipeCreated.class, name = "RecipeCreated"),
    @JsonSubTypes.Type(value = SyncEvent.RecipeDeleted.class, name = "RecipeDeleted"),
    @JsonSubTypes.Type(value = SyncEvent.RecipeContentUpdated.class, name = "RecipeContentUpdated"),
    @JsonSubTypes.Type(value =
            SyncEvent.RecipeIngredientCreated.class, name = "RecipeIngredientCreated"),
    @JsonSubTypes.Type(value =
            SyncEvent.RecipeIngredientDeleted.class, name = "RecipeIngredientDeleted"),
    @JsonSubTypes.Type(value =
            SyncEvent.RecipeIngredientUpdated.class, name = "RecipeIngredientUpdated")
})
sealed public class SyncEvent permits SyncEvent.RecipeCreated, SyncEvent.RecipeDeleted,
        SyncEvent.RecipeContentUpdated,
        SyncEvent.RecipeIngredientCreated, SyncEvent.RecipeIngredientDeleted,
        SyncEvent.RecipeIngredientUpdated{
    private long recipeId;

    /**
     * constructor
     * @param recipeId the recipe concerning the change
     */
    public SyncEvent(long recipeId){
        this.recipeId = recipeId;
    }

    /**
     * empty constructor for object mapper
     */
    public SyncEvent(){}

    public long getRecipeId(){
        return recipeId;
    }

    public void setRecipeId(long recipeId){
        this.recipeId = recipeId;
    }

    public static final class RecipeCreated extends SyncEvent {
        private Recipe createdRecipe;

        /**
         * Empty constructor for object mapper
         */
        public RecipeCreated(){}

        /**
         * constructor
         * @param createdRecipe the created recipe ID
         */
        public RecipeCreated(Recipe createdRecipe){
            super(createdRecipe.getId());
            this.createdRecipe = createdRecipe;
        }

        public Recipe getCreatedRecipe(){
            return createdRecipe;
        }
        public void setCreatedRecipe(Recipe createdRecipe){
            this.createdRecipe = createdRecipe;
        }
    }

    public static final class RecipeDeleted extends SyncEvent {

        /**
         * Empty constructor for object mapper
         */
        public RecipeDeleted(){}

        /**
         * constructor
         * @param recipeId the deleted recipe's id
         */
        public RecipeDeleted(Long recipeId){
            super(recipeId);
        }
    }

    public static final class RecipeContentUpdated extends SyncEvent {
        private Recipe recipe;


        /**
         * Empty constructor for object mapper
         */
        public RecipeContentUpdated(){}

        /**
         * constructor
         * @param recipe the recipe that was updated
         */
        public RecipeContentUpdated(Recipe recipe){
            super(recipe.getId());
            this.recipe = recipe;
        }
        public Recipe getRecipe(){
            return recipe;
        }
        public void setRecipe(Recipe recipe){
            this.recipe = recipe;
        }
    }

    public static final class RecipeIngredientCreated extends SyncEvent {
        private RecipeIngredient createdIngredient;

        /**
         * Empty constructor for object mapper
         */
        public RecipeIngredientCreated(){}

        /**
         * constructor
         * @param createdIngredient the created recipe ID
         */
        public RecipeIngredientCreated(RecipeIngredient createdIngredient){
            super(createdIngredient.getRecipe().getId());
            this.createdIngredient = createdIngredient;
        }

        public RecipeIngredient getCreatedIngredient(){
            return createdIngredient;
        }
        public void setCreatedIngredient(RecipeIngredient createdIngredient){
            this.createdIngredient = createdIngredient;
        }
    }

    public static final class RecipeIngredientDeleted extends SyncEvent {
        private long ingredientId;

        /**
         * Empty constructor for object mapper
         */
        public RecipeIngredientDeleted(){}

        /**
         * constructor
         * @param ingredientId the deleted ingredient's id
         * @param recipeId the deleted ingredient's recipe's id
         */
        public RecipeIngredientDeleted(long ingredientId, long recipeId){
            super(recipeId);
            this.ingredientId = ingredientId;
        }

        public long getIngredientId() {
            return ingredientId;
        }

        public void setIngredientId(long ingredientId) {
            this.ingredientId = ingredientId;
        }
    }
    public static final class RecipeIngredientUpdated extends SyncEvent {

        private RecipeIngredient ingredient;

        /**
         * Empty constructor for object mapper
         */
        public RecipeIngredientUpdated(){}

        /**
         * constructor
         * @param ingredient the ingredient that was updated
         */
        public RecipeIngredientUpdated(RecipeIngredient ingredient){
            super(ingredient.getRecipe().getId());
            this.ingredient = ingredient;
        }
        public RecipeIngredient getIngredient(){
            return ingredient;
        }
        public void setIngredient(RecipeIngredient ingredient){
            this.ingredient = ingredient;
        }
    }
}
