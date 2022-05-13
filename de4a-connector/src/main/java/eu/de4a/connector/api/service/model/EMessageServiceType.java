package eu.de4a.connector.api.service.model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;

/**
 * Enum for controlling correspondences between message types (root XML element
 * name) and DE/DO addresses properties. If something changes on
 * {@link eu.de4a.iem.core} it forces to update this class and keep it up to
 * date
 */
public enum EMessageServiceType
{
  IM (eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestTransferEvidenceIM_QNAME.getLocalPart (), true),
  USI (eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestTransferEvidenceUSI_QNAME.getLocalPart (), true),
  LU (eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestTransferEvidenceLU_QNAME.getLocalPart (), true),
  SN (eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestEventSubscription_QNAME.getLocalPart (), true),
  RESPONSE (eu.de4a.iem.core.jaxb.dt.ObjectFactory._ResponseTransferEvidence_QNAME.getLocalPart (), false),
  SUBSCRIPTION_RESP (eu.de4a.iem.core.jaxb.dt.ObjectFactory._ResponseEventSubscription_QNAME.getLocalPart (), false),
  REDIRECT (eu.de4a.iem.core.jaxb.dt.ObjectFactory._USIRedirectUser_QNAME.getLocalPart (), false),
  NOTIFICATION (eu.de4a.iem.core.jaxb.dt.ObjectFactory._EventNotification_QNAME.getLocalPart (), false);

  private static final Map <String, EMessageServiceType> LOOKUP = new HashMap <> ();
  static
  {
    for (final EMessageServiceType item : values ())
      LOOKUP.put (item.getType (), item);
  }

  private final String type;
  private final boolean isRequest;

  EMessageServiceType (@Nonnull @Nonempty final String type, final boolean isRequest)
  {
    this.type = type;
    this.isRequest = isRequest;
  }

  @Nonnull
  @Nonempty
  public String getType ()
  {
    return type;
  }

  public boolean isRequest ()
  {
    return isRequest;
  }

  @Nonnull
  @Nonempty
  public String getEndpointType ()
  {
    return name ().toLowerCase (Locale.ROOT);
  }

  @Nullable
  public static EMessageServiceType getByTypeOrNull (@Nullable final String type)
  {
    return LOOKUP.get (type);
  }
}
