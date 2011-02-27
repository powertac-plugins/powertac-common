package org.powertac.common.transformer

import org.joda.time.Instant
import org.powertac.common.enumerations.BuySellIndicator
import org.powertac.common.enumerations.CompetitionStatus
import org.powertac.common.enumerations.OrderType
import org.powertac.common.enumerations.ProductType
import org.powertac.common.*

class DomainClassTransformerTests extends GroovyTestCase {

  Competition competition
  Product product
  Timeslot timeslot
  Broker broker
  Shout shout
  String userName
  String apiKey
  DomainClassTransformer domainClassTransformer

  protected void setUp() {
    super.setUp()
    userName = 'testBroker'
    apiKey = 'testApiKey-which-needs-to-be-longer-than-32-characters'
    competition = new Competition(name: "test", enabled: true, current: true, competitionStatus: CompetitionStatus.Finished, description: 'testDescription')
    assert (competition.validate() && competition.save())
    broker = new Broker(competition: competition, userName: userName, apiKey: apiKey)
    assert (broker.validate() && broker.save())
    product = new Product(competition: competition, productType: ProductType.Future)
    assert (product.validate() && product.save())
    timeslot = new Timeslot(serialNumber: 0, startInstant: new Instant(), endInstant: new Instant())
    assert (timeslot.validate() && timeslot.save())
    shout = new Shout(competition: competition, product: product, timeslot: timeslot, broker: broker, quantity: 1.0, limitPrice: 10.0, buySellIndicator: BuySellIndicator.BUY, orderType: OrderType.LIMIT, transactionId: 'testTransaction')
    assert (shout.validate() && shout.save())
    domainClassTransformer = new DomainClassTransformer()
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testCompetitionConversion() {
    String xmlString = domainClassTransformer.toXml(competition)
    def returnValue = domainClassTransformer.fromXml(xmlString)
    assertTrue(returnValue instanceof Competition)
    assertEquals(competition.id, returnValue.id)
    assertEquals(competition.name, returnValue.name)
    assertEquals(competition.current, returnValue.current)
    assertEquals(competition.enabled, returnValue.enabled)
    assertEquals(competition.competitionStatus, returnValue.competitionStatus)
    assertEquals(competition.competitionStatus, returnValue.competitionStatus)
    assertEquals(competition.description, returnValue.description)
  }

  void testBrokerConversion() {
    String xmlString = domainClassTransformer.toXml(broker)
    def returnValue = domainClassTransformer.fromXml(xmlString)
    assertTrue(returnValue instanceof Broker)
    assertEquals(broker.id, returnValue.id)
    assertEquals(broker.userName, returnValue.userName)
    assertEquals(broker.apiKey, returnValue.apiKey)
  }

  void testProductConversion() {
    String xmlString = domainClassTransformer.toXml(product)
    def returnValue = domainClassTransformer.fromXml(xmlString)
    assertTrue(returnValue instanceof Product)
    assertEquals(product.id, returnValue.id)
    assertEquals(product.productType, returnValue.productType)
  }

  void testTimeslotConversion() {
    String xmlString = domainClassTransformer.toXml(timeslot)
    def returnValue = domainClassTransformer.fromXml(xmlString)
    assertTrue(returnValue instanceof Timeslot)
    assertEquals(timeslot.id, returnValue.id)
    assertEquals(timeslot.serialNumber, returnValue.serialNumber)
  }

  void testShoutConversion() {
    String xmlString = domainClassTransformer.toXml(shout)
    def returnValue = domainClassTransformer.fromXml(xmlString)
    assertTrue(returnValue instanceof Shout)
    assertEquals(shout.id, returnValue.id)
    assertEquals(shout.product, returnValue.product)
    assertEquals(shout.timeslot, returnValue.timeslot)
    assertEquals(shout.broker, returnValue.broker)
    assertEquals(shout.quantity, returnValue.quantity)
    assertEquals(shout.limitPrice, returnValue.limitPrice)
    assertEquals(shout.buySellIndicator, returnValue.buySellIndicator)
    assertEquals(shout.transactionId, returnValue.transactionId)
  }

  void testNonXmlStringConversion() {
    String xmlString = "invalid xml"
    assertNull(domainClassTransformer.fromXml(xmlString))
  }

  void testNonDomainClassMarshallingAndUnmarshalling() {
    List testList = ['hallo1', 'hallo2']
    def xmlString = domainClassTransformer.toXml(testList) //should work
    assertNotNull(xmlString)
    assertNull(domainClassTransformer.fromXml(xmlString))
  }
}
