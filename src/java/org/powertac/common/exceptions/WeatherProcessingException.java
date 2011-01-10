package org.powertac.common.exceptions;

/**
 * Thrown if the processing of weather forecasts by a customer fails
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class WeatherProcessingException extends PowerTacException {
  public WeatherProcessingException() {
  }

  public WeatherProcessingException(String s) {
    super(s);
  }

  public WeatherProcessingException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public WeatherProcessingException(Throwable throwable) {
    super(throwable);
  }
}
