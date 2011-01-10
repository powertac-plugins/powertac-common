package org.powertac.common.exceptions;

/**
 * Thrown if tariff processing by a customer fails
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class TariffSubscriptionException extends PowerTacException {
  public TariffSubscriptionException() {
  }

  public TariffSubscriptionException(String s) {
    super(s);
  }

  public TariffSubscriptionException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public TariffSubscriptionException(Throwable throwable) {
    super(throwable);
  }
}
