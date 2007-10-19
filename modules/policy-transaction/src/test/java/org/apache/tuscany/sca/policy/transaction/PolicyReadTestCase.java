package org.apache.tuscany.sca.policy.transaction;

import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

/**
 * 
 * Test the reading of ws config params policy.
 */
public class PolicyReadTestCase extends TestCase {

    @Override
    public void setUp() throws Exception {
    }

    @Override
    public void tearDown() throws Exception {
    }

    public void testPolicyReading() throws Exception {

        TransactionPolicyProcessor processor = new TransactionPolicyProcessor(null);

        URL url = getClass().getResource("/policy_definitions.xml");
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        InputStream urlStream = url.openStream();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(urlStream);

        TransactionPolicy policy = processor.read(reader);
        assertEquals(1200, policy.getTransactionTimeout());
    }

}
