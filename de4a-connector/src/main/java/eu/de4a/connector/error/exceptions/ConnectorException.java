package eu.de4a.connector.error.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import eu.de4a.connector.error.model.ExternalModuleError;
import eu.de4a.connector.error.model.FamilyErrorType;
import eu.de4a.connector.error.model.LayerError;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;

public class ConnectorException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Object entity = null;
    private ExternalModuleError module;
    private FamilyErrorType family;
    private LayerError layer;
    private HttpStatus status = HttpStatus.OK;
    private String code;
    private List<Object> args;
    private Object request;

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
    
    public ConnectorException withRequest(Object request) {
        this.request = request;
        return this;
    }

    public String buildCode() {
        return layer.ordinal() + module.getId() + family.getID();
    }

    @Override
    public String getMessage() {
        return family.getLabel();
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public Object getResponseMessage() {
        return null;
    }

    public List<Object> getArgs() {
        return args;
    }

    public void setArgs(List<Object> args) {
        this.args = args;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(RequestTransferEvidenceUSIIMDRType request) {
        this.request = request;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public FamilyErrorType getFamily() {
        return family;
    }

    public void setFamily(FamilyErrorType family) {
        this.family = family;
    }

    public LayerError getLayer() {
        return layer;
    }

    public void setLayer(LayerError layer) {
        this.layer = layer;
    }

    public ExternalModuleError getModule() {
        return module;
    }

    public void setModule(ExternalModuleError module) {
        this.module = module;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
