package eu.de4a.connector.error.model;

public enum LayerError {
    COMMUNICATIONS(1),
    INTERNAL_FAILURE(2), 
    CONFIGURATION(3);

    private int id;

    LayerError(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }
}
