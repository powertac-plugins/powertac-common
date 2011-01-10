package org.powertac.common.exceptions;

/**
 * Thrown if the revocation of a tariff fails
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class TariffRevokeException extends Exception {
  public TariffRevokeException() {
  }

  public TariffRevokeException(String s) {
    super(s);
  }

  public TariffRevokeException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public TariffRevokeException(Throwable throwable) {
    super(throwable);
  }
}
