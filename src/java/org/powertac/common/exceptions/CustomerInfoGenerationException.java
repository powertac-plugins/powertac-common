package org.powertac.common.exceptions;

/**
 * Thrown if a customer fails to generate his customer info record
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class CustomerInfoGenerationException extends PowerTacException {
  public CustomerInfoGenerationException() {
  }

  public CustomerInfoGenerationException(String s) {
    super(s);
  }

  public CustomerInfoGenerationException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public CustomerInfoGenerationException(Throwable throwable) {
    super(throwable);
  }
}
