package org.powertac.common.exceptions;

/**
 * Thrown if an error occurs during competition shutdown
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class CompetitionFinalizationException extends PowerTacException {
  public CompetitionFinalizationException() {
  }

  public CompetitionFinalizationException(String s) {
    super(s);
  }

  public CompetitionFinalizationException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public CompetitionFinalizationException(Throwable throwable) {
    super(throwable);
  }
}
