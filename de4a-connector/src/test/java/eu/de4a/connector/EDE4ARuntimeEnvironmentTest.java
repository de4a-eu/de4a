package eu.de4a.connector;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Test;

import com.helger.commons.string.StringHelper;
import com.helger.dcng.api.DcngConfig;
import com.helger.peppolid.factory.IIdentifierFactory;

/**
 * Test class for class {@link EDE4ARuntimeEnvironment}.
 *
 * @author Philip Helger
 */
public final class EDE4ARuntimeEnvironmentTest
{
  @Test
  public void testBasic ()
  {
    for (final EDE4ARuntimeEnvironment e : EDE4ARuntimeEnvironment.values ())
    {
      assertTrue (StringHelper.hasText (e.getID ()));
      assertSame (e, EDE4ARuntimeEnvironment.getFromIDOrNull (e.getID ()));
    }
  }

  @Test
  public void testAllowedParticipantIDs ()
  {
    final IIdentifierFactory aIF = DcngConfig.getIdentifierFactory ();
    assertTrue (EDE4ARuntimeEnvironment.PLAYGROUND.isAllowedParticipantID (aIF.createParticipantIdentifier ("", "9999:bla-foo-mock-it2")));
    assertTrue (EDE4ARuntimeEnvironment.PLAYGROUND.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-mock-it2")));
    assertFalse (EDE4ARuntimeEnvironment.PLAYGROUND.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:-mock-it2")));
    assertFalse (EDE4ARuntimeEnvironment.PLAYGROUND.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9991:anything-mock-it2")));
    assertFalse (EDE4ARuntimeEnvironment.PLAYGROUND.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-test-it2")));
    assertFalse (EDE4ARuntimeEnvironment.PLAYGROUND.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-mock-it2:any")));

    assertTrue (EDE4ARuntimeEnvironment.TEST.isAllowedParticipantID (aIF.createParticipantIdentifier ("", "9999:bla-foo-test-it2")));
    assertTrue (EDE4ARuntimeEnvironment.TEST.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-test-it2")));
    assertTrue (EDE4ARuntimeEnvironment.TEST.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9991:anything-test-it2")));
    assertTrue (EDE4ARuntimeEnvironment.TEST.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9900:anything-test-it2")));
    assertFalse (EDE4ARuntimeEnvironment.TEST.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-mock-it2")));
    assertFalse (EDE4ARuntimeEnvironment.TEST.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "8999:anything-test-it2")));
    assertFalse (EDE4ARuntimeEnvironment.TEST.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9899:anything-test-it2")));
    assertFalse (EDE4ARuntimeEnvironment.TEST.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-test-it2:any")));

    // Anything neither playground nor test
    assertFalse (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("", "9999:bla-foo-mock-it2")));
    assertFalse (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-mock-it2")));
    assertTrue (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:-mock-it2")));
    assertTrue (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9991:anything-mock-it2")));
    assertFalse (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-test-it2")));
    assertTrue (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-mock-it2:any")));
    assertFalse (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("", "9999:bla-foo-test-it2")));
    assertFalse (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-test-it2")));
    assertFalse (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9991:anything-test-it2")));
    assertFalse (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9900:anything-test-it2")));
    assertFalse (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-mock-it2")));
    assertTrue(EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "8999:anything-test-it2")));
    assertTrue (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9899:anything-test-it2")));
    assertTrue (EDE4ARuntimeEnvironment.PILOT.isAllowedParticipantID (aIF.createParticipantIdentifier ("any", "9999:anything-test-it2:any")));
}
}
