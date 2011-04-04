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
package org.powertac.common.msg

import org.powertac.common.IdGenerator;

/**
 * Represents a response from server to broker to publication or update
 * of a tariff.
 * @author jcollins
 */
class TariffStatus implements Serializable
{
  enum Status {success, noSuchTariff, noSuchUpdate, illegalOperation,
               invalidTariff, invalidUpdate}
  String id = IdGenerator.createId()
  String brokerUsername
  String tariffId
  String updateId
  String message
  Status status = Status.success

  static constraints = {
    id(nullable: false, blank: false, unique: true)
    brokerUsername(nullable: false, blank: false)
    tariffId(nullable: false, blank: false)
    updateId(nullable: true)
    message(nullable: true)
    status(nullable: false)
  }
  
  static mapping = {
    id (generator: 'assigned')
  }
}
