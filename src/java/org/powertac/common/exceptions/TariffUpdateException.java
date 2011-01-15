package org.powertac.common.exceptions;

/**
 * Thrown if an error occurs if processing of a tariff update in accounting service fails
 *
 * @author Carsten Block
 * @version 1.0, Date: 15.01.11
 */
public class TariffUpdateException extends Exception {

  public TariffUpdateException() {
  }

  public TariffUpdateException(String s) {
    super(s);
  }

  public TariffUpdateException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public TariffUpdateException(Throwable throwable) {
    super(throwable);
  }
}
