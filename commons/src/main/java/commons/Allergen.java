package commons;

public enum Allergen {
    GLUTEN("Gluten", "#F4D35E"),
    MILK("Milk / Dairy", "#B8D8FF"),
    EGGS("Eggs", "#FFD6A5"),
    PEANUTS("Peanuts", "#D4A373"),
    TREE_NUTS("Tree nuts", "#CDB4DB"),
    SOY("Soy", "#B7E4C7"),
    FISH("Fish", "#A0C4FF"),
    SHELLFISH("Shellfish", "#FFADAD"),
    SESAME("Sesame", "#E9EDC9"),
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
