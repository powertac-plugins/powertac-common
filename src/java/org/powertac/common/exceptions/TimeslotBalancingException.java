package org.powertac.common.exceptions;

/**
 * Thrown if the distribution utility fails to balance a timeslot.
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class TimeslotBalancingException extends PowerTacException {
  public TimeslotBalancingException() {
  }

  public TimeslotBalancingException(String s) {
    super(s);
  }

  public TimeslotBalancingException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public TimeslotBalancingException(Throwable throwable) {
    super(throwable);
  }
}
