package eu.de4a.connector.as4.servlet;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import com.helger.dcng.api.DcngConfig;
import com.helger.dcng.core.DcngInit;
import com.helger.dcng.webapi.DcngApiInit;
import com.helger.photon.api.IAPIRegistry;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.audit.DoNothingAuditor;
import com.helger.photon.core.servlet.WebAppListener;
import eu.de4a.connector.as4.handler.IncomingAS4PKHandler;

public class AS4ServletListener extends WebAppListener {

    @Override
    protected String getDataPath(@Nonnull final ServletContext aSC) {
        String ret = DcngConfig.WebApp.getDataPath();
        if (ret == null) {
            // Fall back to servlet context path
            ret = super.getDataPath(aSC);
        }
        return ret;
    }

    @Override
    protected String getServletContextPath(final ServletContext aSC) {
        try {
            return super.getServletContextPath(aSC);
        } catch (final IllegalStateException ex) {
            // E.g. "Unpack WAR files" in Tomcat is disabled
            return getDataPath(aSC);
        }
    }

    @Override
    protected void afterContextInitialized(final ServletContext aSC) {
        // Use default handler
        DcngInit.initGlobally(aSC, new IncomingAS4PKHandler());

        // Don't write audit logs
        AuditHelper.setAuditor(new DoNothingAuditor(() -> "none"));
    }

    @Override
    protected void initAPI(@Nonnull final IAPIRegistry aAPIRegistry) {
        DcngApiInit.initAPI(aAPIRegistry);
    }

    @Override
    protected void beforeContextDestroyed(final ServletContext aSC) {
        DcngInit.shutdownGlobally(aSC);
    }
}
