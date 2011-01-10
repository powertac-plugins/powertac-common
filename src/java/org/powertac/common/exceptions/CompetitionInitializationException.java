package org.powertac.common.exceptions;

/**
 * Thrown if competition intialization fails
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class CompetitionInitializationException extends Exception {
  public CompetitionInitializationException() {
  }

  public CompetitionInitializationException(String s) {
    super(s);
  }

  public CompetitionInitializationException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public CompetitionInitializationException(Throwable throwable) {
    super(throwable);
  }
}
