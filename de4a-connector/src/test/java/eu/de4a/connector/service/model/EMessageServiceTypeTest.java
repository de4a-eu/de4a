package eu.de4a.connector.service.model;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Locale;

import org.junit.Test;

import eu.de4a.connector.api.service.model.EMessageServiceType;

/**
 * Test class for class {@link EMessageServiceType}.
 *
 * @author Philip Helger
 */
public final class EMessageServiceTypeTest
{
  @Test
  public void testBasic ()
  {
    for (final EMessageServiceType e : EMessageServiceType.values ())
    {
      assertSame (e, EMessageServiceType.getByElementLocalNameOrNull (e.getElementLocalName ()));
      assertNull (EMessageServiceType.getByElementLocalNameOrNull (e.getElementLocalName ().toUpperCase (Locale.ROOT)));
    }
    assertNull (EMessageServiceType.getByElementLocalNameOrNull ("bla"));
    assertNull (EMessageServiceType.getByElementLocalNameOrNull (null));
    assertNull (EMessageServiceType.getByElementLocalNameOrNull (""));
  }
}
