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

import org.powertac.common.exceptions.TariffRuleException;

/**
 * Tariff rule enforcer interface defines common methods a Tariff Rule Enforcer module in PowerTAC competition has to implement
 *
 * @author Carsten Block
 * @version 0.1 - January 2nd, 2011
 */
public interface TariffRuleEnforcer extends CompetitionBaseEvents {

  /**
   * Evaluates a given tariff by checking tariff objects against a
   * pre-defined set of commonly agreed PowerTAC market rules. Only
   * tariff objects that comply with these rules can pass this filter.
   *
   * @param tariff object
   * @return whether tariffDoReply is accepted or not
   * @throws org.powertac.common.exceptions.TariffRuleException thrown if an error occurs during tariff validation. Note that non-conformant tariffs do *not* cause an exception but simply result in "false" being returned. Exceptions should only occur of non tariff objects are provided or if, e.g., database access is broken.
   */
  public boolean acceptTariff(Object tariff) throws TariffRuleException;

}
