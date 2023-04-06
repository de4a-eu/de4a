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
package eu.de4a.connector.as4.servlet;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;

import com.helger.dcng.api.DcngConfig;
import com.helger.dcng.core.DcngInit;
import com.helger.dcng.webapi.DcngApiInit;
import com.helger.photon.api.IAPIRegistry;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.audit.DoNothingAuditor;
import com.helger.photon.core.servlet.WebAppListener;

import eu.de4a.connector.StaticContextAccessor;
import eu.de4a.connector.as4.handler.IncomingAS4PKHandler;

@WebListener
public class AS4ServletListener extends WebAppListener
{

  @Override
  protected String getDataPath (@Nonnull final ServletContext aSC)
  {
    String ret = DcngConfig.WebApp.getDataPath ();
    if (ret == null)
    {
      // Fall back to servlet context path
      ret = super.getDataPath (aSC);
    }
    return ret;
  }

  @Override
  protected String getServletContextPath (final ServletContext aSC)
  {
    try
    {
      return super.getServletContextPath (aSC);
    }
    catch (final IllegalStateException ex)
    {
      // E.g. "Unpack WAR files" in Tomcat is disabled
      return getDataPath (aSC);
    }
  }

  @Override
  protected void afterContextInitialized (final ServletContext aSC)
  {
    // Don't write audit logs
    AuditHelper.setAuditor (new DoNothingAuditor ( () -> "none"));

    // Use default handler
    // Set an indirection level because I can't get the Spring CDI to work
    DcngInit.initGlobally (aSC, x -> StaticContextAccessor.getBean (IncomingAS4PKHandler.class).handleIncomingRequest (x));
  }

  @Override
  protected void initAPI (@Nonnull final IAPIRegistry aAPIRegistry)
  {
    // I don't think we need this here
    if (false)
      DcngApiInit.initAPI (aAPIRegistry);
  }

  @Override
  protected void beforeContextDestroyed (final ServletContext aSC)
  {
    DcngInit.shutdownGlobally (aSC);
  }
}
