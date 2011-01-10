package org.powertac.common.command

/**
 * Command object that can be used to report a server error to a client (i.e. broker.
 *
 * @author Carsten Block
 * @version 1.0, Date: 07.01.11
 */
class ErrorCmd implements Serializable {
  String className
  String message
  String cause
  String stackTrace

  public static ErrorCmd fromException (Exception ex) {
    if (!ex) return null
    return new ErrorCmd(className: ex.class.getName(), message: ex.message, cause: ex.cause.toString(), stackTrace: ex.stackTrace.toArrayString())
  }
}
