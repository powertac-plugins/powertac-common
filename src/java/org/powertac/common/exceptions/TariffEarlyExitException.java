package org.powertac.common.exceptions;

/**
 * Thrown if an Early Exit from a tariff subscription changes
 *
 * @author Carsten Block
 * @version 1.0, Date: 14.01.11
 */
public class TariffEarlyExitException extends Exception {
  public TariffEarlyExitException() {
  }

  public TariffEarlyExitException(String s) {
    super(s);
  }

  public TariffEarlyExitException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public TariffEarlyExitException(Throwable throwable) {
    super(throwable);
  }
}
