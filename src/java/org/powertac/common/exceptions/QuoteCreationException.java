package org.powertac.common.exceptions;

/**
 * Thrown if the liquidity provider fails to generate a quote (i.e. a correlated buy and sell order)
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class QuoteCreationException extends PowerTacException {
  public QuoteCreationException() {
  }

  public QuoteCreationException(String s) {
    super(s);
  }

  public QuoteCreationException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public QuoteCreationException(Throwable throwable) {
    super(throwable);
  }
}
