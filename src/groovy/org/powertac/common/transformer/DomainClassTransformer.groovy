package org.powertac.common.transformer

import grails.converters.XML
import groovy.util.slurpersupport.GPathResult
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.web.converters.ConverterUtil
import org.codehaus.groovy.grails.web.converters.XMLParsingParameterCreationListener
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.*

/**
 * Provides two simple transformer methods for domain
 * classes using grails XML converter to generate
 * xml string representations of objects [method fromXML()]
 * or domain class object representations from xml strings [method toXML()]
 *
 * @author Carsten Block
 * @version 1.0 , Date: 23.01.11
 */
class DomainClassTransformer {

  final dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

  DomainClassTransformer() {
    //marshallers *should* be registered by JodaTime plugin automatically...
    //But if we do not register them here it won't work... Probably a bug...
    XML.registerObjectMarshaller(DateTime, 1) {
      it?.toString(dateTimeFormatter.withZone(it?.zone))
    }
    XML.registerObjectMarshaller(LocalDate, 2) {
      it?.toString("yyyy-MM-dd")
    }
    XML.registerObjectMarshaller(LocalTime, 3) {
      it?.toString("HH:mm:ss")
    }
    XML.registerObjectMarshaller(LocalDateTime, 4) {
      it?.toString("yyyy-MM-dd'T'HH:mm:ss")  //Use ISO Time Format for cross platform compatibility
    }
    XML.registerObjectMarshaller(DateTimeZone, 5) {
      it?.ID
    }
  }

  static final LOG = LogFactory.getLog(XMLParsingParameterCreationListener)

  /**
   * Converts a domain object into its corresponding xml string representation
   *
   * @param o the domain class to marshal into xml string representation
   * @return String containing the xml representation of the domain object or null if serialization fails (logging the corresponding conversion error)
   */
  public String toXml(Object o) {
    if (!o) return null
    StringWriter writer = new StringWriter()
    def xml = new XML(o)
    xml.render(writer)
    return writer.toString()
  }

  /**
   * Attempts to create a domain class instance based on the provided xml string
   *
   * @param xmlString the string representation from which a domain class instance should be created
   * @return domain class instance, created from the xml description of the domain class or null if the unmarshalling fails (logging the corresponding conversion error)
   */
  public Object fromXml(String xmlString) {
    try {
      GPathResult xml = new XmlSlurper().parseText(xmlString);
      if (!xml) throw new ConverterException("Failed to unmarshall xml: XML parsing result from '$xmlString' was null.")
      String domainClassName = ConverterUtil.getDomainClassNames().find {it.toLowerCase().contains(xml.name().toLowerCase())}
      if (!domainClassName) throw new ConverterException("Failed to unmarshall xml: '${xml.name()}' not found in the set of powertac domain classes.")
      def domainClass = ApplicationHolder.application.getClassForName(domainClassName)
      if (!domainClass) throw new ConverterException("Failed to unmarshall xml: ${domainClassName} could not be resolved as domain class.")
      def map = [:]
      def id = xml.@id.text()
      populateParamsFromXML(xml, map)
      def target = [:]
      createFlattenedKeys(map, map, target)
      for (entry in target) {
        if (!map[entry.key]) {
          map[entry.key] = entry.value
        }
      }
      def domainInstance = domainClass.newInstance(map)
      if (id) domainInstance.id = id
      return domainInstance

    } catch (Exception e) {
      LOG.error "Error parsing incoming XML request: ${e.message}", e
      return null
    }
  }


  private populateParamsFromXML(xml, map) {
    for (child in xml.children()) {
      // one-to-ones have ids
      if (child.@id.text()) {
        map["${child.name()}.id"] = child.@id.text()
        def childMap = [:]
        map[child.name()] = childMap
        populateParamsFromXML(child, childMap)
      }
      else {
        map[child.name()] = child.text()
      }
    }
  }

  /**
   * Populates the target map with current map using the root map to form a nested prefix
   * so that a hierarchy of maps is flattened.
   */
  private createFlattenedKeys(Map root, Map current, Map target, prefix = '') {
    for (entry in current) {
      if (entry.value instanceof Map) {
        createFlattenedKeys(root, entry.value, target, "${entry.key}.")
      }
      else if (prefix) {
        target["${prefix}${entry.key}"] = entry.value
      }
    }
  }

}
