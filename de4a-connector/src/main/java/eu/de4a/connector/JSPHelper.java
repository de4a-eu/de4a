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
package eu.de4a.connector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.string.StringHelper;
import com.helger.dcng.api.DcngConfig;

public final class JSPHelper
{
  private JSPHelper ()
  {}

  @Nonnull
  public static String formatted (@Nullable final String s)
  {
    String ret = "<span class=\"param-value";
    if ("true".equals (s))
      ret += " value-true";
    else
      if ("false".equals (s))
        ret += " value-false";
      else
        if (StringHelper.hasText (s))
          ret += " value";
    ret += "\">";

    if (StringHelper.hasText (s))
      ret += s;
    else
      ret += "<em>not set</em>";
    ret += "</span>";
    return ret;
  }

  @Nonnull
  public static String formattedProp (@Nullable final String sProp)
  {
    return formatted (DcngConfig.getConfig ().getAsString (sProp));
  }
}
