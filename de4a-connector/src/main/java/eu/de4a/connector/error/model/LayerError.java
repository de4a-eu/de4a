package eu.de4a.connector.error.model;

public enum LayerError {
    COMMUNICATIONS("1"),
    INTERNAL_FAILURE("2"), 
    CONFIGURATION("3");

    private String id;

    LayerError(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }
}
