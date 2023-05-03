/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Austrian Federal Computing Center (BRZ)
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.de4a.connector.error.exceptions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.springframework.http.HttpStatus;

import eu.de4a.connector.error.model.EFamilyErrorType;
import eu.de4a.connector.error.model.ELayerError;
import eu.de4a.connector.error.model.ErrorHelper;
import eu.de4a.kafkaclient.model.EExternalModule;
import eu.de4a.kafkaclient.model.ELogMessageLevel;

public class ConnectorException extends RuntimeException
{
  private ELayerError layer;
  private EExternalModule module;
  private EFamilyErrorType family;
  private HttpStatus status = HttpStatus.BAD_REQUEST;
  private List <Object> args;

  @Nullable
  public ELayerError getLayer ()
  {
    return layer;
  }

  public ConnectorException withLayer (final ELayerError layer)
  {
    this.layer = layer;
    return this;
  }

  @Nullable
  public EExternalModule getModule ()
  {
    return module;
  }

  public ConnectorException withModule (final EExternalModule module)
  {
    this.module = module;
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
    return ErrorHelper.createCode (module, ELogMessageLevel.ERROR, family);
  }

  @Override
  public String getMessage ()
  {
    return family.getLabel ();
  }
}
