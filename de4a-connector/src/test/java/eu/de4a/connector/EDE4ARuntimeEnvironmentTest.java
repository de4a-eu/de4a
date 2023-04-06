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
