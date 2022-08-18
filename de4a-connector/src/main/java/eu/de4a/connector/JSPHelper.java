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
