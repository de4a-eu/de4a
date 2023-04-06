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
import eu.de4a.kafkaclient.model.EExternalModule;
import eu.de4a.kafkaclient.model.ELogMessageLevel;

import com.helger.commons.annotation.Nonempty;

public class ErrorHelper
{
  private ErrorHelper ()
  {}

  @Nonnull
  @Nonempty
  public static String createCode (@Nonnull final EExternalModule eExtMod,
		  						   @Nonnull final ELogMessageLevel level,
                                   @Nonnull final EFamilyErrorType eFamily)
  {
    return eExtMod.getID () + level.getCode() + eFamily.getID ();
  }
}
