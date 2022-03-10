package eu.de4a.connector.error.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class ConnectorException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    @NonNull
    private ExternalModuleError module;
    @NonNull
    private FamilyErrorType family;
    @NonNull
    private LayerError layer;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private String code;
    private List<Object> args;

    public ConnectorException withModule(ExternalModuleError module) {
        this.module = module;
        return this;
    }

    public ConnectorException withLayer(LayerError layer) {
        this.layer = layer;
        return this;
    }

    public ConnectorException withFamily(FamilyErrorType family) {
        this.family = family;
        return this;
    }

    public ConnectorException withHttpStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public ConnectorException withMessageArgs(List<Object> args) {
        this.args = args;
        return this;
    }

    public ConnectorException withMessageArg(Object arg) {
        if (args == null) {
            args = new ArrayList<>();
        }
        args.add(arg);
        return this;
    }

    public String buildCode() {
        return layer.ordinal() + module.getId() + family.getID();
    }

    @Override
    public String getMessage() {
        return family.getLabel();
    }
}
