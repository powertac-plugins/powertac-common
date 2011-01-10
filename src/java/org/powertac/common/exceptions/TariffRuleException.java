package org.powertac.common.exceptions;

/**
 * Thrown if an error occurs during the validation of tariff property conformity with market rules
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class TariffRuleException extends PowerTacException {
  public TariffRuleException() {
  }

  public TariffRuleException(String s) {
    super(s);
  }

  public TariffRuleException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public TariffRuleException(Throwable throwable) {
    super(throwable);
  }
}
