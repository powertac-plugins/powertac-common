/*
 * Copyright (c) 2011 by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powertac.common

/**
 * Represents a generic state change. Domain classes should create and 
 * save one of these when they change state and there is no other record
 * in the database that would indicate the change. So for example, if 
 * a change happens because of an incoming message (which is saved in
 * the database), then it need not be recorded with a StateChange. On the
 * other hand, if a state change happens because of a method call, then
 * there is no other record of the change. 
 * @author John Collins
 */
class StateChange {
  
  String objectType // the type of object being changed
  String objectId // ID of object being changed
  String property // name of property being changed
  def oldValue // previous value
  def newValue // new value

  static constraints = {
    objectType(nullable: false)
    objectId(nullable: false)
    property(nullable: false)
    oldValue(nullable: true)
    newValue(nullable: true)
  }
}
