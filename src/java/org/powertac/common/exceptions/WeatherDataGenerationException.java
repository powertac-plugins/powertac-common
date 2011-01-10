package org.powertac.common.exceptions;

/**
 * Thrown if the {@link org.powertac.common.interfaces.PhysicalEnvironment} fails to create weather forecasts
 *
 * @author Carsten Block
 * @version 1.0, Date: 10.01.11
 */
public class WeatherDataGenerationException extends PowerTacException {
  public WeatherDataGenerationException() {
  }

  public WeatherDataGenerationException(String s) {
    super(s);
  }

  public WeatherDataGenerationException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public WeatherDataGenerationException(Throwable throwable) {
    super(throwable);
  }
}
