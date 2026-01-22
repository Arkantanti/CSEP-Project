package commons;

public enum Allergen {
    GLUTEN("Gluten", "#ECC112"),
    MILK("Milk / Dairy", "#4A97FC"),
    EGGS("Eggs", "#F09319"),
    PEANUTS("Peanuts", "#D4A373"),
    TREE_NUTS("Tree nuts", "#4C8422"),
    SOY("Soy", "#66E193"),
    FISH("Fish", "#5E73DC"),
    SHELLFISH("Shellfish", "#F96A6A"),
    SESAME("Sesame", "#875A27"),
    MUSTARD("Mustard", "#FFD60A");

    private final String displayName;
    private final String color;

    /**
     * Constructor.
     * @param displayName Human friendly name.
     * @param color Tag color associated with the allergen.
     */
    Allergen(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}
