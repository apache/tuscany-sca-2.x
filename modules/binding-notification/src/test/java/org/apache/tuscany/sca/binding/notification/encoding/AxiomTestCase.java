package org.apache.tuscany.sca.binding.notification.encoding;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.junit.Assert;

import junit.framework.TestCase;

public class AxiomTestCase extends TestCase {

    private static String wsnt = "http://docs.oasis-open.org/wsn/b-2";
    private static String wsa = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    private static String testUrl1 = "http://localhost:8081/test";
    private static String testUrl2 = "http://localhost:8082/test";
    private static String testNewProducerResponse =
        "<wsnt:NewProducerResponse xmlns:wsnt=\"" + wsnt + "\" ConsumerSequenceType=\"EndConsumers\">" +
            "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl1 + "</wsa:Address>" +
            "<wsa:Address xmlns:wsa=\"" + wsa + "\">" + testUrl2 + "</wsa:Address>" +
        "</wsnt:NewProducerResponse>";
    
    public void testAxiom() {
        try {
            StAXOMBuilder builder = new StAXOMBuilder(new ByteArrayInputStream(testNewProducerResponse.getBytes()));
            OMElement element = builder.getDocumentElement();
            Assert.assertNotNull(element);
            
            StringWriter sw = new StringWriter();
            element.serialize(sw);
            sw.flush();
            Assert.assertEquals(sw.toString(),testNewProducerResponse);
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }
}
