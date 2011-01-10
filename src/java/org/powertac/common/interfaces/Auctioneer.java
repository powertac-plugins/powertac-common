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

import org.powertac.common.Shout;
import org.powertac.common.command.ShoutDoCreateCmd;
import org.powertac.common.command.ShoutDoDeleteCmd;
import org.powertac.common.command.ShoutDoUpdateCmd;
import org.powertac.common.exceptions.MarketClearingException;
import org.powertac.common.exceptions.ShoutCreationException;
import org.powertac.common.exceptions.ShoutDeletionException;
import org.powertac.common.exceptions.ShoutUpdateExeption;

import java.util.List;

/**
 * Interface that defines the publicly accessible methods
 * a Power TAC auctioneer has to implement.
 *
 * @author Carsten Block
 * @version 0.1, Date: 01.12.10
 */
public interface Auctioneer extends CompetitionBaseEvents {

  /**
   * Process an incoming shout, e.g. by matching it against existing shouts
   * As a CDA market auctioneer this is the method to match the <code>shoutDoCreate</shout>
   * against existing shouts in order to execute it immediately. As a call (PDA) market
   * auctioner this method could be used to store the new shout and to wait for a
   * <code>Auctioneer.clearMarket()</code> method invocation in order to start the matching
   * and execution process.
   * <p/>
   * In both cases orderbook updates, quote updates, shout status updates etc may be created
   * and returned in a list so that other components (e.g. {@link AccountingService})
   * can further process these objects later on.
   *
   * @param shoutDoCreate new incoming shout from a broker
   * @return List of objects, which might include {@link org.powertac.common.CashUpdate}, {@link org.powertac.common.Orderbook},{@link org.powertac.common.TransactionLog},{@link Shout}. These may then processed further "downstream" in the server, e.g. by accounting service
   * @throws org.powertac.common.exceptions.ShoutCreationException thrown if the creation of the new shout fails
   */
  List processShoutCreate(ShoutDoCreateCmd shoutDoCreate) throws ShoutCreationException;

  /**
   * Deletes the shout specified by {@link Shout}
   *
   * @param shoutDoDeleteCmd command object that contains the shoutId that should be deleted
   * @return Shout object that contains the new status of the deleted shout
   * @throws org.powertac.common.exceptions.ShoutDeletionException thrown if the shout deletion fails
   */
  Shout processShoutDelete(ShoutDoDeleteCmd shoutDoDeleteCmd) throws ShoutDeletionException;

  /**
   * Updates the shout specified by the {@link ShoutDoUpdateCmd}. Changeable
   * shout attributes are limit price and quantity only.
   *
   * @param shoutDoUpdateCmd the shout object to update
   * @return a list of updated shout objects if a matching took place based on the shout change or null otherwise
   * @throws org.powertac.common.exceptions.ShoutUpdateExeption thrown if the shout update fails
   */
  List processShoutUpdate(ShoutDoUpdateCmd shoutDoUpdateCmd) throws ShoutUpdateExeption;

  /**
   * This method is required for periodic clearing auctions and essentially tells
   * the Auctioneer module to clear the market matching all open shouts in the orderbooks
   *
   * @return a list of {@link Shout}, {@link org.powertac.common.command.PositionDoUpdateCmd}, and {@link org.powertac.common.command.CashDoUpdateCmd} objects
   * @throws org.powertac.common.exceptions.MarketClearingException thrown if the market clearing fails
   */
  List clearMarket() throws MarketClearingException;

}
