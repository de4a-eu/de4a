package eu.de4a.connector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.regex.RegExHelper;
import com.helger.peppolid.IParticipantIdentifier;

public enum EDE4ARuntimeEnvironment implements IHasID <String>
{
  PLAYGROUND ("playground") {
    @Override
    public boolean isAllowedParticipantID (@Nonnull final IParticipantIdentifier aPI)
    {
      final String sValue = aPI.getValue ();
      return RegExHelper.stringMatchesPattern ("9999:.+-mock-it2", sValue);
    }
  },
  TEST ("test") {
    @Override
    public boolean isAllowedParticipantID (@Nonnull final IParticipantIdentifier aPI)
    {
      final String sValue = aPI.getValue ();
      return RegExHelper.stringMatchesPattern ("99\\d\\d:.+-test-it2", sValue);
    }
  },
  PILOT ("pilot") {
    @Override
    public boolean isAllowedParticipantID (@Nonnull final IParticipantIdentifier aPI)
    {
      // The ones that are neither playground nor test are pilot ones :)
      return !PLAYGROUND.isAllowedParticipantID (aPI) && !TEST.isAllowedParticipantID (aPI);
    }
  };

  private final String m_sID;

  EDE4ARuntimeEnvironment (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  public abstract boolean isAllowedParticipantID (@Nonnull IParticipantIdentifier aPI);

  @Nullable
  public static EDE4ARuntimeEnvironment getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EDE4ARuntimeEnvironment.class, sID);
  }
}
