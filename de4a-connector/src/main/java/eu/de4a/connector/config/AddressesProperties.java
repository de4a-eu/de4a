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
package eu.de4a.connector.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.json.IJson;
import com.helger.json.IJsonObject;
import com.helger.json.serialize.JsonReader;

/**
 * Mapping class for the external services URLs <br>
 * Check local file "de-do.json".
 */
@Component
public class AddressesProperties
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AddressesProperties.class);

  private final Map <String, Map <String, String>> dataOwners = new LinkedHashMap <> ();
  private final Map <String, Map <String, String>> dataEvaluators = new LinkedHashMap <> ();

  @Value ("${de4a.addressfile.prefix:de-do}")
  private String m_sFilePrefix;

  public AddressesProperties ()
  {}

  private static void _fillMap (@Nonnull final Map <String, Map <String, String>> aTarget,
                                @Nullable final IJsonObject aJson,
                                @Nonnull final String sChildName)
  {
    if (aJson == null)
      throw new IllegalStateException ("The DE/DO address JSON is missing the top level child '" + sChildName + "'");

    // Ensure to start fresh
    aTarget.clear ();

    // Add all from JSON
    for (final Map.Entry <String, IJson> aEntry : aJson)
      if (aEntry.getValue ().isObject ())
      {
        final Map <String, String> aMap1 = aTarget.computeIfAbsent (aEntry.getKey (), k -> new HashMap <> ());
        for (final Map.Entry <String, IJson> aEntry2 : aEntry.getValue ().getAsObject ())
          aMap1.put (aEntry2.getKey (), aEntry2.getValue ().getAsValue ().getAsString ());
      }

    LOGGER.info ("Successfully read the '" + sChildName + "' address map with " + aTarget.size () + " entries");
  }

  @PostConstruct
  public void init ()
  {
    ValueEnforcer.notNull (m_sFilePrefix, "FilePrefix");
    final ClassPathResource aRes = new ClassPathResource (m_sFilePrefix + ".json");
    LOGGER.info ("Reading DE/DO address JSON from " + aRes);
    final IJsonObject aJson = JsonReader.builder ()
                                        .source (aRes)
                                        .customizeCallback (p -> p.setTrackPosition (true))
                                        .readAsObject ();
    if (aJson == null)
      throw new IllegalStateException ("Failed to read DE/DO JSON from " + aRes);

    _fillMap (dataOwners, aJson.getAsObject ("dataOwners"), "dataOwners");
    _fillMap (dataEvaluators, aJson.getAsObject ("dataEvaluators"), "dataEvaluators");
  }

  @Nonnull
  public Map <String, Map <String, String>> getDataOwners ()
  {
    return dataOwners;
  }

  @Nullable
  public String getDataOwnerByType (final String doID, final String endpointType)
  {
    return this.dataOwners.get (doID).get (endpointType);
  }

  @Nonnull
  public Map <String, Map <String, String>> getDataEvaluators ()
  {
    return dataEvaluators;
  }

  @Nullable
  public String getDataEvaluatorByType (final String deID, final String endpointType)
  {
    return this.dataEvaluators.get (deID).get (endpointType);
  }
}
