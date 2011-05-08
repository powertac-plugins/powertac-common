package org.powertac.common.command

/**
 * Command object to be used by brokers to log in to the web-app
 * @author David Dauer
 */
class LoginRequestCmd implements Serializable {
  String username
  String password
}





