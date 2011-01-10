package org.powertac.common.exceptions;

/**
 * Thrown if an error occurs during competition reset
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class CompetitionResetException extends PowerTacException {

  public CompetitionResetException() {
  }

  public CompetitionResetException(String s) {
    super(s);
  }

  public CompetitionResetException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public CompetitionResetException(Throwable throwable) {
    super(throwable);
  }
}
