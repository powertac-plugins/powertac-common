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
import org.powertac.common.TariffSpecification;
import org.powertac.common.Tariff;
import org.powertac.common.command.*;
import org.powertac.common.exceptions.*;

import java.util.List;

/**
 * Common interface for the PowerTAC accounting service.
 * The accouting service module is owner of the {@link org.powertac.common.CashUpdate},
 * {@link org.powertac.common.PositionUpdate}, {@link org.powertac.common.MeterReading},
 * and {@link org.powertac.common.Tariff} database
 * tables. It's main purpose is to do the bookeeping throught the competition by
 * manipulating the above mentioned database tables recording cash, position, tariff or
 * metering changes.
 *
 * @author Carsten Block
 * @version 0.2 - January 14th, 2011
 */
public interface AccountingService {

  /**
   * Processes {@link PositionDoUpdateCmd} objects adjusting the booked amounts
   * of a product (e.g. energy futures) for particular broker and a particular timeslot
   *
   * @param positionDoUpdateCmd the object that describes what position change to book in the database
   * @return PositionUpdate Latest {@link PositionUpdate} which contains relative change, new overall balance, origin and reason for the position change
   * @throws org.powertac.common.exceptions.PositionUpdateException is thrown if a position updated fails
   */
  public PositionUpdate processPositionUpdate (PositionDoUpdateCmd positionDoUpdateCmd) throws PositionUpdateException;

  /**
   * Processes {@link CashDoUpdateCmd} objects adjusting the booked amounts of cash for a specific broker.
   * @param cashDoUpdateCmd the object that describes what cash change to book in the database
   * @return CashUpdate Latest {@link CashUpdate} which contains relative change, new overall balance, origin and reason for the cash update
   * @throws org.powertac.common.exceptions.CashUpdateException is thrown if a cash update fails
   */
  public CashUpdate processCashUpdate(CashDoUpdateCmd cashDoUpdateCmd) throws CashUpdateException;

  /**
   * Processes incoming {@link TariffSpecification} of a broker, and turns it into a Tariff instance.
   *
   * @param tariffDoPublishCmd command object that contains the tariff detais to be published
   * @throws org.powertac.common.exceptions.TariffPublishException is thrown if the tariff publishing fails
   * @return the published tariff object
   */
  public Tariff processTariffPublished(TariffSpecification tariffSpec) throws TariffPublishException;


  /**
   * Method processes incoming {@link TariffDoReplyCmd} of a broker or customer. The main task
   * of this method is to persistently record the tariffDoReplyCmd and then to forward it
   * downstream for further processing.
   *
   * @param tariffDoReplyCmd the tariff reply to store in the database
   * @return the latest tariff instance
   * @throws org.powertac.common.exceptions.TariffReplyException is thrown if the tariff publishing fails
   */
  //public Tariff processTariffReply(TariffDoReplyCmd tariffDoReplyCmd) throws TariffReplyException;

  /**
   * Method processes incoming {@link TariffDoRevokeCmd} of a broker. This method needs to
   * implement logic that leads to the given tariff being revoked from the list of
   * published tariffs.
   *
   * @param tariffDoRevokeCmd describing the tariff to be revoked
   * @return Tariff updated tariff object that reflects the revocation of the tariff
   * @throws org.powertac.common.exceptions.TariffRevokeException is thrown if the tariff publishing fails
   */
  public Tariff processTariffRevoke(TariffDoRevokeCmd tariffDoRevokeCmd) throws TariffRevokeException;


  /**
   * Method processes incoming {@link TariffDoSubscribeCmd}. This method implements the
   * logic required to make a customer subscribe to a particular tariff given either
   * (i) a published or (ii) an individually agreed tariff instance to subscribe to.
   *
   * @param tariffDoSubscribeCmd contains references to the subscribing customer and to the tariff instance to subscribe to
   * @return List of objects which can include {@link CashUpdate} and {@link Tariff}. The tariff object reflects the subscription of the customer defined in the {@link TariffDoSubscribeCmd} while the (optional) {@link CashUpdate} contains the cash booking of the (optional) signupFee into the broker's cash account
   * @throws TariffSubscriptionException is thrown if the subscription fails
   */
  public List processTariffSubscribe (TariffDoSubscribeCmd tariffDoSubscribeCmd) throws TariffSubscriptionException;

  /**
   * Method processes incoming {@link TariffDoRevokeCmd}. The method implements the logic required to unsubscribe a customer from a tariff ahead of the originally agreed contract end.
   * @param tariffDoEarlyExitCmd contains references to the customer who wishes to exit the tariff contract ahead of time as well as to the tariff contract that should be cancelled.
   * @return List of objects which can include {@link CashUpdate} and {@link Tariff}. The tariff object reflects the cancellation of the tariff subscription while the (optional) {@link CashUpdate} contains the booking of the early exit fee into the broker's cash account
   * @throws TariffEarlyExitException is thrown if the tariff contract cancellation fails.
   */
  //public List processTariffEarlyExit(TariffDoEarlyExitCmd tariffDoEarlyExitCmd) throws TariffEarlyExitException;

  /**
   * Method processes incoming {@link TariffDoUpdateCmd}. The method implements the logic required to update the conditions of an existing tariff for all subscribed customers.
   * @param tariffDoUpdateCmd contains the new (revised) tariff conditions
   * @return List of {@link Tariff} objects which reflect the updated individual subscriptions for
   * all customers subscribed to the updated tariff
   * @throws TariffUpdateException is thrown if the tariff updating fails.
   */
  //public List processTariffUpdate(TariffDoUpdateCmd tariffDoUpdateCmd) throws TariffUpdateException;

  /**
   * Returns a list of all currently active (i.e. subscribeable) tariffs (which might be empty)
   *
   * @return a list of all active tariffs, which might be empty if no tariffs are published
   */
  public List<Tariff> publishTariffList();


  /**
   * Publishes the list of available customers (which might be empty)
   *
   * @return a list of all available customers, which might be empty if no customers are available
   */
  public List<Customer> publishCustomersAvailable();

}
