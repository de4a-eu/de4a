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
package eu.de4a.connector.error.model;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;

public enum EFamilyErrorType
{
  CONNECTION_ERROR ("01", CMessageKeys.ERROR_CONNECTION),
  ERROR_RESPONSE ("02", CMessageKeys.ERROR_RESPONSE),
  CONVERSION_ERROR ("03", CMessageKeys.ERROR_CONVERSION),
  SAVING_DATA_ERROR ("04", CMessageKeys.ERROR_DATABASE),
  MISSING_REQUIRED_ARGUMENTS ("05", CMessageKeys.ERROR_MISSING_ARGS),
  SERVICE_NOT_FOUND ("06", CMessageKeys.ERROR_SERVICE_NOT_FOUND),
  SCHEMA_VALIDATION_FAILED ("07", CMessageKeys.ERROR_SCHEME_VALIDATION),
  AS4_ERROR_COMMUNICATION ("08", CMessageKeys.ERROR_AS4_COMMUNICATION);

  private final String id;
  private final String label;

  EFamilyErrorType (@Nonnull @Nonempty final String id, @Nonnull @Nonempty final String label)
  {
    this.id = id;
    this.label = label;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return id;
  }

  @Nonnull
  @Nonempty
  public String getLabel ()
  {
    return label;
  }
}
