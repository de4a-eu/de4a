package eu.de4a.connector.error.exceptions;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.http.HttpStatus;
import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;

public class ConnectorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private ExternalModuleError module;
    private FamilyErrorType family;
    private LayerError layer;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private String code;
    private List<Object> args;

    @Nullable
    public ExternalModuleError getModule ()
    {
        return module;
    }


    public ConnectorException withModule(final ExternalModuleError module) {
        this.module = module;
        return this;
    }

    public ConnectorException withLayer(final LayerError layer) {
        this.layer = layer;
        return this;
    }

    public ConnectorException withFamily(final FamilyErrorType family) {
        this.family = family;
        return this;
    }

    @Nullable
    public HttpStatus getStatus ()
    {
      return this.status;
    }

    public ConnectorException withHttpStatus(final HttpStatus status) {
        this.status = status;
        return this;
    }

    @Nullable
    public List<Object> getArgs (){
      return args;
    }

    public ConnectorException withMessageArgs(final List<Object> args) {
        this.args = args;
        return this;
    }

    public ConnectorException withMessageArg(final Object arg) {
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
