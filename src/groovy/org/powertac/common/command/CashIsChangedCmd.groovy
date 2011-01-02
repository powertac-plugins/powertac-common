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



package org.powertac.common.command

import org.joda.time.LocalDateTime

 /**
 * Command object that represents an cash transaction
 * (add / deduce money) that should be executed on a
 * specific broker cash account for a given reason.
 *
 * @author Carsten Block
 * @version 1.0 , Date: 02.12.10
 */
class CashIsChangedCmd implements Serializable {
  String id
  String competitionId
  String transactionId
  String brokerId
  BigDecimal relativeChange
  BigDecimal overallBalance
  String reason
  String origin
  LocalDateTime dateCreated = new LocalDateTime()
}
