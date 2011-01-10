package org.powertac.common.exceptions;

/**
 * Thrown if an error occurs during the tax authority subsidizing brokers.
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class SubsidyException extends PowerTacException {
  public SubsidyException() {
  }

  public SubsidyException(String s) {
    super(s);
  }

  public SubsidyException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public SubsidyException(Throwable throwable) {
    super(throwable);
  }
}
