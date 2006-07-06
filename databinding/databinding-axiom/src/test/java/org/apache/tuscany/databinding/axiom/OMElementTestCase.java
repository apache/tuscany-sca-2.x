package org.apache.tuscany.databinding.axiom;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;

public class OMElementTestCase extends TestCase {
    private static final String IPO_XML = "<?xml version=\"1.0\"?>" + "<ipo:purchaseOrder"
    + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + "  xmlns:ipo=\"http://www.example.com/IPO\""
    + "  xsi:schemaLocation=\"http://www.example.com/IPO ipo.xsd\"" + "  orderDate=\"1999-12-01\">"
    + "  <shipTo exportCode=\"1\" xsi:type=\"ipo:UKAddress\">" + "    <name>Helen Zoe</name>" + "    <street>47 Eden Street</street>"
    + "    <city>Cambridge</city>" + "    <postcode>CB1 1JR</postcode>" + "  </shipTo>" + "  <billTo xsi:type=\"ipo:USAddress\">"
    + "    <name>Robert Smith</name>" + "    <street>8 Oak Avenue</street>" + "    <city>Old Town</city>" + "    <state>PA</state>"
    + "    <zip>95819</zip>" + "  </billTo>" + "  <items>" + "    <item partNum=\"833-AA\">"
    + "      <productName>Lapis necklace</productName>" + "      <quantity>1</quantity>" + "      <USPrice>99.95</USPrice>"
    + "      <ipo:comment>Want this for the holidays</ipo:comment>" + "      <shipDate>1999-12-05</shipDate>" + "    </item>" + "  </items>"
    + "</ipo:purchaseOrder>";

    public final void testTransform() {
        String2OMElement t1 = new String2OMElement();
        OMElement element = t1.transform(IPO_XML, null);
        OMElement2String t2= new OMElement2String();
        String xml = t2.transform(element, null);
        Assert.assertNotNull(xml);
        Assert.assertNotNull(xml.indexOf("<ipo:comment>")!=-1);
    }

}
