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

import org.powertac.common.Competition;
import org.powertac.common.command.CashDoUpdateCmd;
import org.powertac.common.exceptions.SubsidyException;
import org.powertac.common.exceptions.TaxingException;

import java.util.List;

/**
 * TaxAuthority interface defines how a tax authority module
 * can tax brokers during PowerTAC competition.
 *
 * @author Carsten Block
 * @version 0.1 - January 2, 2011
 */
public interface TaxAuthority extends CompetitionBaseEvents {

  /**
   * @param competition the competition in which brokers should be taxed
   * @return List of CashDoUpdateCmd objects specifying which brokers to tax for which purpose. This list can then be processed by the accounting service who does the actual booking of the cash changes
   * @throws org.powertac.common.exceptions.TaxingException thrown if an error occurs during the tax authority taxing brokers.
   */
  public List<CashDoUpdateCmd> taxBrokers(Competition competition) throws TaxingException;

  /**
   * @param competition the competition in which brokers should be subsidized
   * @return List of CashDoUpdateCmd objects specifying which brokers to subsidise and for which purpose. This list can then be processed by the accounting service who does the actual booking of the cash changes
   * @throws org.powertac.common.exceptions.SubsidyException thrown if an error occurs during the tax authority subsidizing brokers.
   */
  public List<CashDoUpdateCmd> subsidiseBrokers(Competition competition) throws SubsidyException;
}
