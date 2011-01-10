package org.powertac.common.exceptions;

/**
 * Thrown if a Shout is not found
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class ShoutNotFoundException extends PowerTacException {

  public ShoutNotFoundException() {
  }

  public ShoutNotFoundException(String s) {
    super(s);
  }

  public ShoutNotFoundException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public ShoutNotFoundException(Throwable throwable) {
    super(throwable);
  }
}
