package org.powertac.common.exceptions;

/**
 * Thrown if a tariff processing fails
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class TariffPublishException extends PowerTacException {
  public TariffPublishException() {
  }

  public TariffPublishException(String s) {
    super(s);
  }

  public TariffPublishException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public TariffPublishException(Throwable throwable) {
    super(throwable);
  }
}
