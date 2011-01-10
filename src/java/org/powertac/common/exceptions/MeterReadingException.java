package org.powertac.common.exceptions;

/**
 * Thrown if the generation of a meter reading record through a customer fails
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class MeterReadingException extends PowerTacException {
  public MeterReadingException() {
  }

  public MeterReadingException(String s) {
    super(s);
  }

  public MeterReadingException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public MeterReadingException(Throwable throwable) {
    super(throwable);
  }
}
