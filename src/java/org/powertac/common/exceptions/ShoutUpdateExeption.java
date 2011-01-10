package org.powertac.common.exceptions;

/**
 * Thrown if a shout update fails
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class ShoutUpdateExeption extends PowerTacException {
  public ShoutUpdateExeption() {
  }

  public ShoutUpdateExeption(String s) {
    super(s);
  }

  public ShoutUpdateExeption(String s, Throwable throwable) {
    super(s, throwable);
  }

  public ShoutUpdateExeption(Throwable throwable) {
    super(throwable);
  }
}
