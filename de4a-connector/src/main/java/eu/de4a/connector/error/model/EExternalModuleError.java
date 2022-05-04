package eu.de4a.connector.error.model;

import javax.annotation.Nonnull;
import com.helger.commons.annotation.Nonempty;

public enum EExternalModuleError {
    IDK("01", "IDK"),
    SMP("02", "SMP"),
    CONNECTOR_DR("03", "CONNECTOR DR"),
    CONNECTOR_DT("04", "CONNECTOR DT"),
    DATA_OWNER("05", "DATA OWNER"),
    DATA_EVALUATOR("06", "DATA EVALUATOR"),
    AS4("07", "AS4 GATEWAY"),
    MOR("08", "MOR"),
    NONE("00", "NONE");

    private final String id;
    private final String label;

    EExternalModuleError(@Nonnull @Nonempty final String id, @Nonnull @Nonempty final String label) {
        this.id = id;
        this.label = label;
    }

    @Nonnull @Nonempty
    public String getId() {
        return id;
    }

    @Nonnull @Nonempty
    public String getLabel() {
        return label;
    }

}
