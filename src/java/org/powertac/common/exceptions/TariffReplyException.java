package org.powertac.common.exceptions;

/**
 * Thrown if a the processing of a tariff reply fails
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class TariffReplyException extends PowerTacException {
  public TariffReplyException() {
  }

  public TariffReplyException(String s) {
    super(s);
  }

  public TariffReplyException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public TariffReplyException(Throwable throwable) {
    super(throwable);
  }
}
