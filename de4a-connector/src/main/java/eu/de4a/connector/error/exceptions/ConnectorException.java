package eu.de4a.connector.error.exceptions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.springframework.http.HttpStatus;

import eu.de4a.connector.error.model.EExternalModuleError;
import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;

public class ConnectorException extends RuntimeException
{
  private EExternalModuleError module;
  private EFamilyErrorType family;
  private ELayerError layer;
  private HttpStatus status = HttpStatus.BAD_REQUEST;
  private List <Object> args;

  @Nullable
  public EExternalModuleError getModule ()
  {
    return module;
  }

  public ConnectorException withModule (final EExternalModuleError module)
  {
    this.module = module;
    return this;
  }

  public ConnectorException withLayer (final ELayerError layer)
  {
    this.layer = layer;
    return this;
  }

  public ConnectorException withFamily (final EFamilyErrorType family)
  {
    this.family = family;
    return this;
  }

  @Nullable
  public HttpStatus getStatus ()
  {
    return this.status;
  }

  public ConnectorException withHttpStatus (final HttpStatus status)
  {
    this.status = status;
    return this;
  }

  @Nullable
  public List <Object> getArgs ()
  {
    return args;
  }

  public ConnectorException withMessageArgs (final List <Object> args)
  {
    this.args = args;
    return this;
  }

  public ConnectorException withMessageArg (final Object arg)
  {
    if (args == null)
    {
      args = new ArrayList <> ();
    }
    args.add (arg);
    return this;
  }

  public String buildCode ()
  {
    return layer.ordinal () + module.getId () + family.getID ();
  }

  @Override
  public String getMessage ()
  {
    return family.getLabel ();
  }
}
