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
package eu.de4a.connector.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.helger.commons.collection.ArrayHelper;

import eu.de4a.connector.StaticContextAccessor;
import eu.de4a.iem.core.jaxb.common.EventNotificationItemType;
import eu.de4a.iem.core.jaxb.common.EventSubscripRequestItemType;
import eu.de4a.iem.core.jaxb.common.RedirectUserType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceItemType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceLUItemType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionItemType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractEvidenceItemType;

public final class MessageUtils
{

  private MessageUtils ()
  {}

  public static String format (final String key, final Object [] args)
  {
    final MessageSource messageSource = StaticContextAccessor.getBean (MessageSource.class);
    final Locale locale = LocaleContextHolder.getLocale ();
    if (args != null && args.length > 0)
    {
      return messageSource.getMessage (key, args, locale);
    }
    return messageSource.getMessage (key, ArrayHelper.EMPTY_OBJECT_ARRAY, locale);
  }

  public static <T> String getRequestMetadata (final List <T> items)
  {
    final List <String> requestItems = new ArrayList <> ();
    items.forEach (item -> {
      final RequestEvidenceItemType request = (RequestEvidenceItemType) item;
      requestItems.add (request.getRequestItemId () + ":" + request.getCanonicalEvidenceTypeId ());
    });

    return format (requestItems);
  }
  
  public static String getLegacyRequestMetadata (final String requestId, final String canonicalEvidenceTypeId)
  {
    final List <String> requestItems = new ArrayList <> ();
    requestItems.add (requestId + ":" + canonicalEvidenceTypeId);
    return format (requestItems);
  }
  
  public static String getSubscriptionRequestMetadata (final List <EventSubscripRequestItemType> items)
  {
    final List <String> requestItems = new ArrayList <> ();
    items.forEach (item -> {
      requestItems.add (item.getRequestItemId () + ":" + item.getCanonicalEventCatalogUri());
    });

    return format (requestItems);
  }
  
  public static String getLookupRequestMetadata (final List<RequestEvidenceLUItemType> items)
  {
    final List <String> requestItems = new ArrayList <> ();
    items.forEach (item -> {
      requestItems.add (item.getRequestItemId () + ":" + item.getCanonicalEvidenceTypeId());
    });

    return format (requestItems);
  }
  
  
  
  public static String getEvidenceResponseMetadata (final List <ResponseExtractEvidenceItemType> items)
  {
    final List <String> requestItems = new ArrayList <> ();
    items.forEach (item -> {
      if (!item.getError ().isEmpty ())
        requestItems.add (item.getRequestItemId () + ":" + item.getErrorAtIndex (0).getCode ());
      else
        requestItems.add (item.getRequestItemId () + ":" + item.getCanonicalEvidenceTypeId ());
    });

    return format (requestItems);
  }

  public static String getRedirectResponseMetadata (final RedirectUserType redirectResponse)
  {
    var data = redirectResponse.getError ().isEmpty () ? redirectResponse.getRequestId () + ":" + redirectResponse.getCanonicalEvidenceTypeId () 
    													: redirectResponse.getRequestId () + ":" + redirectResponse.getErrorAtIndex (0).getCode ();
    return "(" + data + ")";
  }

  public static String getEventNotificationMetadata (final List <EventNotificationItemType> items)
  {
    final List <String> requestItems = new ArrayList <> ();
    items.forEach (item -> {
      requestItems.add (item.getNotificationItemId () + ":" + item.getCanonicalEventCatalogUri ());
    });

    return format (requestItems);

  }

   public static String getEventSubscriptionResponseMetadata(List<ResponseEventSubscriptionItemType> items) 
   { 
	   final List <String> requestItems = new ArrayList <> ();
	   items.forEach (item -> {
	      if (!item.getError ().isEmpty ())
	        requestItems.add (item.getRequestItemId () + ":" + item.getErrorAtIndex (0).getCode ());
	      else
	        requestItems.add (item.getRequestItemId () + ":" + item.getCanonicalEventCatalogUri());
	    });

	    return format (requestItems);
   }

  private static String format (final List <String> items)
  {
    return "(" + String.join (", ", items) + ")";
  }
}
