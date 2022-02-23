package eu.de4a.connector.as4.servlet;

import java.util.Properties;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.helger.phase4.mgr.MetaAS4Manager;
import com.helger.photon.api.IAPIRegistry;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.audit.DoNothingAuditor;
import com.helger.photon.core.servlet.WebAppListener;
import com.helger.photon.security.login.LoggedInUserManager;

import eu.de4a.connector.as4.handler.NothingIncomingAS4PKHandler;
import eu.toop.connector.api.TCConfig;
import eu.toop.connector.app.TCInit;
import eu.toop.connector.webapi.TCAPIInit;

/**
 * Global startup etc. listener.
 *
 * @author Philip Helger
 */
public class TCWebAppListener extends WebAppListener
{
  public TCWebAppListener ()
  {
    setHandleStatisticsOnEnd (false);
  }
  
  @Override
  protected void logStartupInfo (@Nonnull final ServletContext aSC)
  {
    logLogo ();
    logServerInfo (aSC);
    logClassPath ();
    logInitParameters (aSC);
    logThirdpartyModules ();
    logJMX ();
  }

  @Override
  protected String getDataPath (@Nonnull final ServletContext aSC)
  {
    String ret = TCConfig.WebApp.getDataPath ();
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
    // Use default handler
	  NothingIncomingAS4PKHandler handler= WebApplicationContextUtils.getRequiredWebApplicationContext(aSC)
			  .getBean(NothingIncomingAS4PKHandler.class);

    // Don't write audit logs
    AuditHelper.setAuditor (new DoNothingAuditor (LoggedInUserManager.getInstance ()));
    
    //Default implementation and DSD service for TOOP legacy
    Properties props = System.getProperties();
    props.setProperty("toop.mem.implementation", "phase4");
    props.setProperty("toop.dsd.service.baseurl", "http://dsd.dev.exchange.toop.eu");
    
    TCConfig.getConfig().reloadAllResourceBasedConfigurationValues();
    TCInit.initGlobally (aSC, handler);
    
    // Set de4a1 profile as default
    if(!"de4a1".equals(MetaAS4Manager.getProfileMgr().getDefaultProfile().getID())) {
        MetaAS4Manager.getProfileMgr().setDefaultProfile(
                MetaAS4Manager.getProfileMgr().getProfileOfID("de4a1"));
    }
  }

  @Override
  protected void initAPI (@Nonnull final IAPIRegistry aAPIRegistry)
  {
    TCAPIInit.initAPI (aAPIRegistry);
  }

  @Override
  protected void beforeContextDestroyed (final ServletContext aSC)
  {
    TCInit.shutdownGlobally (aSC);
  }
}
