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
package eu.de4a.connector.api.service.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.string.StringHelper;

/**
 * Enum for controlling correspondences between message types (root XML element
 * name) and DE/DO addresses properties. If something changes on
 * {@link eu.de4a.iem.core} it forces to update this class and keep it up to
 * date
 */
public enum EMessageServiceType
{
  IM ("im", eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestTransferEvidenceIM_QNAME.getLocalPart (), true),
  USI ("usi", eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestTransferEvidenceUSI_QNAME.getLocalPart (), true),
  LU ("lu", eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestTransferEvidenceLU_QNAME.getLocalPart (), true),
  SN ("sn", eu.de4a.iem.core.jaxb.dr.ObjectFactory._RequestEventSubscription_QNAME.getLocalPart (), true),
  RESPONSE ("response", eu.de4a.iem.core.jaxb.dt.ObjectFactory._ResponseTransferEvidence_QNAME.getLocalPart (), false),
  SUBSCRIPTION_RESP ("subscription_resp", eu.de4a.iem.core.jaxb.dt.ObjectFactory._ResponseEventSubscription_QNAME.getLocalPart (), false),
  REDIRECT ("redirect", eu.de4a.iem.core.jaxb.dt.ObjectFactory._USIRedirectUser_QNAME.getLocalPart (), false),
  NOTIFICATION ("notification", eu.de4a.iem.core.jaxb.dt.ObjectFactory._EventNotification_QNAME.getLocalPart (), false);

  private final String endpointType;
  private final String elementLocalName;
  private final boolean isRequest;

  EMessageServiceType (@Nonnull @Nonempty final String endpointType, @Nonnull @Nonempty final String elementLocalName, final boolean isRequest)
  {
    this.endpointType = endpointType;
    this.elementLocalName = elementLocalName;
    this.isRequest = isRequest;
  }

  @Nonnull
  @Nonempty
  public String getEndpointType ()
  {
    return endpointType;
  }

  @Nonnull
  @Nonempty
  public String getElementLocalName ()
  {
    return elementLocalName;
  }

  public boolean isRequest ()
  {
    return isRequest;
  }

  @Nullable
  public static EMessageServiceType getByElementLocalNameOrNull (@Nullable final String elementLocalName)
  {
    if (StringHelper.hasNoText (elementLocalName))
      return null;
    return EnumHelper.findFirst (EMessageServiceType.class, x-> x.getElementLocalName ().equals (elementLocalName));
  }
}
