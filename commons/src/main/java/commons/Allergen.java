package commons;

public enum Allergen {
    GLUTEN("Gluten"),
    MILK("Milk / Dairy"),
    EGGS("Eggs"),
    PEANUTS("Peanuts"),
    TREE_NUTS("Tree nuts"),
    SOY("Soy"),
    FISH("Fish"),
    SHELLFISH("Shellfish"),
    SESAME("Sesame"),
    MUSTARD("Mustard");

    private final String displayName;

    /**
     * Constructor.
     * @param displayName Human friendly name.
     */
    Allergen(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
