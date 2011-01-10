package org.powertac.common.exceptions;

/**
 * Thrown if a tariff processing fails
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class TariffProcessingException extends Exception {
  public TariffProcessingException() {
  }

  public TariffProcessingException(String s) {
    super(s);
  }

  public TariffProcessingException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public TariffProcessingException(Throwable throwable) {
    super(throwable);
  }
}
