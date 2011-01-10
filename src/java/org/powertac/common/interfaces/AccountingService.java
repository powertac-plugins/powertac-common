/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.powertac.common.interfaces;

import org.powertac.common.CashUpdate;
import org.powertac.common.PositionUpdate;
import org.powertac.common.Tariff;
import org.powertac.common.command.*;

import java.util.List;

/**
 * Common interface for the PowerTAC accounting service.
 * The accouting service module is owner of the {@link org.powertac.common.CashUpdate},
 * {@link org.powertac.common.PositionUpdate}, {@link org.powertac.common.MeterReading},
 * {@link org.powertac.common.Customer}, and {@link org.powertac.common.Tariff} database
 * tables. It's main purpose is to do the bookeeping throught the competition by
 * manipulating the above mentioned database tables recording cash, position, tariff or
 * metering changes.
 *
 * @author Carsten Block
 * @version 0.1 - January 2nd, 2011
 */
public interface AccountingService extends CompetitionBaseEvents {

  /**
   * Method processes positionDoUpdateCmd objects adjusting the booked amounts
   * of a product (e.g. energy futures) for particular broker and a particular timeslot
   *
   * @param positionDoUpdateCmd the object that describes what position change to book in the database
   * @return PositionUpdate Latest {@link PositionUpdate} which contains relative change, new overall balance, origin and reason for the position change
   */
  public PositionUpdate processPositionUpdate (PositionDoUpdateCmd positionDoUpdateCmd);

  /**
   * Method processes cashDoUpdateCmd objects adjusting the booked amounts of cash for a specific broker.
   * @param cashDoUpdateCmd the object that describes what cash change to book in the database
   * @return CashUpdate Latest {@link CashUpdate} which contains relative change, new overall balance, origin and reason for the cash update
   */
  public CashUpdate processCashUpdate(CashDoUpdateCmd cashDoUpdateCmd);

  /**
   * Method processes incoming tariffDoPublishCmd of a broker. The method does not
   * return any results objects as tariffs are published only periodically through the
   * {@code publishTariffList()} method
   *
   * @param tariffDoPublishCmd
   */
  public void processTariffPublished(TariffDoPublishCmd tariffDoPublishCmd);


  /**
   * Method processes incoming tariffDoReplyCmd of a broker or customer. The main task
   * of this method is to persistently record the tariffDoReplyCmd and then to forward it
   * downstream for further processing.
   *
   * @param tariffDoReplyCmd the tariff reply to store in the database
   * @return the processed tariffDoReplyCmd object
   */
  public TariffDoReplyCmd processTariffReply(TariffDoReplyCmd tariffDoReplyCmd);

  /**
   * Method processes incoming tariffDoRevokeCmd of a broker. This method needs to
   * implement logic that leads to the given tariff being revoked from the list of
   * published tariffs.
   *
   * @param tariffDoRevokeCmd describing the tariff to be revoked
   * @return Tariff updated tariff object that reflects the revocation of the tariff
   */
  public Tariff processTariffRevoke(TariffDoRevokeCmd tariffDoRevokeCmd);


  /**
   * Returns a list of all currently active (i.e. subscribable) tariffs
   *
   * @return a list of all active tariffs
   */
  List<Tariff> publishTariffList();


  /**
   * Publishes the list of available customers
   *
   * @return the identical customerInfo parameter
   */
  List<Customer> publishCustomersAvailable();

}
