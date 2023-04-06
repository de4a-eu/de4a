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

import javax.annotation.concurrent.Immutable;

@Immutable
public final class CMessageKeys
{
  /*
   * Errors message keys
   */
  public static final String ERROR_OWNER_NOT_FOUND = "error.owner.not.found";
  public static final String ERROR_SERVICE_NOT_FOUND = "error.service.not.found";
  public static final String ERROR_MISSING_ARGS = "error.missing.arguments";
  public static final String ERROR_CONNECTION = "error.connection";
  public static final String ERROR_RESPONSE = "error.response";
  public static final String ERROR_CONVERSION = "error.conversion";
  public static final String ERROR_DATABASE = "error.database";
  public static final String ERROR_SCHEME_VALIDATION = "error.schema.validation";
  public static final String ERROR_AS4_COMMUNICATION = "error.as4.communications";

  public static final String ERROR_404 = "error.404";
  public static final String ERROR_400_MIMETYPE = "error.400.mimetype";
  public static final String ERROR_400_UNMARSHALLING = "error.400.args.unmarshalling";
  public static final String ERROR_400_ARGS_REQUIRED = "error.400.args.required";

  private CMessageKeys ()
  {}
}
