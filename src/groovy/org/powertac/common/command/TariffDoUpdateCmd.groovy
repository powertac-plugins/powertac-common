package org.powertac.common.command

import org.codehaus.groovy.grails.validation.Validateable
import org.powertac.common.Constants
import org.powertac.common.Tariff

/**
 * TODO: Add Description
 *
 * @author Carsten Block
 * @version 1.0, Date: 15.01.11
 */
@Validateable class TariffDoUpdateCmd extends GenericTariffCmd {

  Tariff parent

  static constraints = {
    parent(nullable: false, validator: {val->
      if (!val?.isDynamic) return [Constants.TARIFF_NON_DYNAMIC]
      return  true
    })
  }
}
