package org.powertac.common

import org.powertac.common.command.TariffDoPublishCmd
import org.powertac.common.enumerations.TariffState

/**
 * TariffBuilder allows the easy creation of Tariff objects using
 * method chaining.
 *
 * Example:
 * TariffReply tr = TariffBuilder.withTariff(tariff1).setSignupFee(0.2).build()
 *
 * @author Carsten Block
 * @version 1.0 , Date: 13.01.11
 */
public class TariffBuilder {

  Tariff tariff

  /*
  * Private constructor to be invoked from the static factory methods only
  */
  private TariffBuilder(Tariff tariff) {
    this.tariff = tariff;
  }

  public static TariffBuilder fromTariffDoPublishCmd(TariffDoPublishCmd tariffDoPublishCmd) {
    return new TariffBuilder(new Tariff(tariffDoPublishCmd.properties))
  }

  public static TariffBuilder fromTariff(Tariff tariff) {
    return new TariffBuilder(tariff);
  }

  public TariffBuilder setTariffState(TariffState tariffState) {
    this.tariff.tariffState = tariffState;
    return this;
  }

  public Tariff buildTariff() {
    return tariff;
  }

}
