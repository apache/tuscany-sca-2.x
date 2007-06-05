package org.apache.tuscany.sca.databinding.json;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.sca.databinding.json.JSON2XMLStreamReader;
import org.apache.tuscany.sca.databinding.json.XMLStreamReader2JSON;
import org.apache.tuscany.sca.databinding.json.XMLStreamSerializer;
import org.apache.tuscany.sca.databinding.json.axiom.JSON2OMElement;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.codehaus.jettison.json.JSONObject;

public class JSONTransformerTestCase extends TestCase {
    private static final String IPO_XML = "<?xml version=\"1.0\"?>" + "<ipo:purchaseOrder"
                                          + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                                          + "  xmlns:ipo=\"http://www.example.com/IPO\""
                                          + "  xsi:schemaLocation=\"http://www.example.com/IPO ipo.xsd\""
                                          + "  orderDate=\"1999-12-01\">"
                                          + "  <shipTo exportCode=\"1\" xsi:type=\"ipo:UKAddress\">"
                                          + "    <name>Helen Zoe</name>"
                                          + "    <street>47 Eden Street</street>"
                                          + "    <city>Cambridge</city>"
                                          + "    <postcode>CB1 1JR</postcode>"
                                          + "  </shipTo>"
                                          + "  <billTo xsi:type=\"ipo:USAddress\">"
                                          + "    <name>Robert Smith</name>"
                                          + "    <street>8 Oak Avenue</street>"
                                          + "    <city>Old Town</city>"
                                          + "    <state>PA</state>"
                                          + "    <zip>95819</zip>"
                                          + "  </billTo>"
                                          + "  <items>"
                                          + "    <item partNum=\"833-AA\">"
                                          + "      <productName>Lapis necklace</productName>"
                                          + "      <quantity>1</quantity>"
                                          + "      <USPrice>99.95</USPrice>"
                                          + "      <ipo:comment>Want this for the holidays</ipo:comment>"
                                          + "      <shipDate>1999-12-05</shipDate>"
                                          + "    </item>"
                                          + "  </items>"
                                          + "</ipo:purchaseOrder>";

    private static final String JSON_STR = "{\"xsl:root\":{\"@xmlns\":{\"xsl\":\"http://foo.com\"},\"data\":{\"$\":\"my json string\"}}}";

    public void testXML2JSON() throws Exception {
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(IPO_XML));
        XMLStreamReader2JSON t1 = new XMLStreamReader2JSON();
        JSONObject json = t1.transform(reader, null);
        Assert.assertNotNull(json);

        // Cannot round-trip as we hit a bug in Jettison
        /*
         JSON2XMLStreamReader t2 = new JSON2XMLStreamReader();
         XMLStreamReader reader2 = t2.transform(json, null);
         StringWriter sw = new StringWriter();
         XMLStreamWriter streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
         new XMLStreamSerializer().serialize(reader2, streamWriter);
         streamWriter.flush();
         System.out.println(sw.toString());
         */

    }

    public void testJSON2XML() throws Exception {
        JSON2XMLStreamReader t2 = new JSON2XMLStreamReader();
        XMLStreamReader reader2 = t2.transform(new JSONObject(JSON_STR), null);
        StringWriter sw = new StringWriter();
        XMLStreamWriter streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
        new XMLStreamSerializer().serialize(reader2, streamWriter);
        streamWriter.flush();
        Assert.assertTrue(sw.toString()
            .contains("<xsl:root xmlns:xsl=\"http://foo.com\"><data>my json string</data></xsl:root>"));
    }

    public void testJSON2OMElement() throws Exception {
        JSON2OMElement t1 = new JSON2OMElement();
        TransformationContext context = new TransformationContextImpl();
        DataType dt = new DataTypeImpl(Object.class, new XMLType(new QName("http://foo.com", "root"), null));
        context.setTargetDataType(dt);
        OMElement element = t1.transform(new JSONObject(JSON_STR), context);
        StringWriter writer = new StringWriter();
        element.serialize(writer);
        // System.out.println(writer.toString());
    }

    public void testString2JSON() throws Exception {
        String json = "{\"name\":\"John\",\"age\":25}";
        String2JSON t1 = new String2JSON();
        JSONObject jsonObject = t1.transform(json, null);
        assertEquals(jsonObject.getString("name"), "John");
        assertEquals(jsonObject.getInt("age"), 25);
        JSON2String t2 = new JSON2String();
        String str = t2.transform(jsonObject, null);
        assertEquals(json, str);
    }
}
