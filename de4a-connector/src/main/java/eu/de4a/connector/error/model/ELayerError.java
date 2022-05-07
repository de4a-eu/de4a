package eu.de4a.connector.error.model;

import javax.annotation.Nonnull;
import com.helger.commons.annotation.Nonempty;

public enum ELayerError {
    COMMUNICATIONS("1"),
    INTERNAL_FAILURE("2"),
    CONFIGURATION("3");

    private final String id;

    ELayerError(@Nonnull @Nonempty final String id) {
        this.id = id;
    }

    @Nonnull @Nonempty
    public String getID() {
        return id;
    }
}
