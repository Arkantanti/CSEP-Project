package commons;

public enum Unit {

    GRAM, LITER, CUSTOM;
    @Override
    public String toString() {
        return name().toLowerCase()+"s";
    }
}
