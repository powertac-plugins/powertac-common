package org.powertac.common.exceptions;

/**
 * Thrown if a Competition is Not Found
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class CompetitionNotFoundException extends PowerTacException {

  public CompetitionNotFoundException() {
  }

  public CompetitionNotFoundException(String s) {
    super(s);
  }

  public CompetitionNotFoundException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public CompetitionNotFoundException(Throwable throwable) {
    super(throwable);
  }
}
