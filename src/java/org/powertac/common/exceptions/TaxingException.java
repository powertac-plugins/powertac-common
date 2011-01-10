package org.powertac.common.exceptions;

/**
 * Thrown if an error occurs during the tax authority taxing brokers.
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class TaxingException extends PowerTacException {
  public TaxingException() {
  }

  public TaxingException(String s) {
    super(s);
  }

  public TaxingException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public TaxingException(Throwable throwable) {
    super(throwable);
  }
}
