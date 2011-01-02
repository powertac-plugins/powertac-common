/*
 * Copyright 2009-2011 the original author or authors.
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

package org.powertac.common.command;


import org.joda.time.LocalDateTime

/**
 * An OrderbookIsChangedCmd represents an orderbook state change
 * up to the depth of 10 orders on the buy and sell side.
 *
 * @author Carsten Block
 * @version 1.0 , Date: 01.12.10
 */
class OrderbookIsChangedCmd {
  String id
  String competitionId
  LocalDateTime dateExecuted
  String transactionId
  String productId
  String timeslotId
  Boolean latest
  BigDecimal bid0 
  BigDecimal bid1 
  BigDecimal bid3 
  BigDecimal bid2 
  BigDecimal bid4 
  BigDecimal bid5 
  BigDecimal bid6 
  BigDecimal bid7 
  BigDecimal bid8 
  BigDecimal bid9 
  BigDecimal bidSize0 
  BigDecimal bidSize1 
  BigDecimal bidSize2 
  BigDecimal bidSize3 
  BigDecimal bidSize4 
  BigDecimal bidSize5 
  BigDecimal bidSize6 
  BigDecimal bidSize7 
  BigDecimal bidSize8 
  BigDecimal bidSize9 
  BigDecimal ask0 
  BigDecimal ask1 
  BigDecimal ask2 
  BigDecimal ask3 
  BigDecimal ask4 
  BigDecimal ask5 
  BigDecimal ask6 
  BigDecimal ask7 
  BigDecimal ask8 
  BigDecimal ask9 
  BigDecimal askSize0 
  BigDecimal askSize1 
  BigDecimal askSize2 
  BigDecimal askSize3 
  BigDecimal askSize4 
  BigDecimal askSize5 
  BigDecimal askSize6 
  BigDecimal askSize7 
  BigDecimal askSize8 
  BigDecimal askSize9
}
