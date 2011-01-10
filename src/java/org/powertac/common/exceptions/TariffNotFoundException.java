package org.powertac.common.exceptions;

/**
 * Thrown if a tariff lookup in the db fails
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class TariffNotFoundException extends Exception {
  public TariffNotFoundException() {
  }

  public TariffNotFoundException(String s) {
    super(s);
  }

  public TariffNotFoundException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public TariffNotFoundException(Throwable throwable) {
    super(throwable);
  }
}
