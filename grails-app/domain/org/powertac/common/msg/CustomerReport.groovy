package org.powertac.common.msg

class CustomerReport {

  String name
  BigDecimal powerUsage

  String toString() {
    "${name} ${powerUsage}"
  }

  static constraints = {
  }
}
