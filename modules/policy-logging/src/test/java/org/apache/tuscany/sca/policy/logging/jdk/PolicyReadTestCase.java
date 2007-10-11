package org.apache.tuscany.sca.policy.logging.jdk;


import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.policy.logging.jdk.JDKLoggingPolicy;
import org.apache.tuscany.sca.policy.logging.jdk.JDKLoggingPolicyProcessor;

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
        JDKLoggingPolicyProcessor processor = new JDKLoggingPolicyProcessor(null);
        
        URL url = getClass().getResource("mock_policies.xml");
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        
        InputStream urlStream = url.openStream();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(urlStream);
        
        
        JDKLoggingPolicy policy = processor.read(reader);
        assertEquals(policy.getLoggerName(), "test.logger");
        assertEquals(policy.getLogLevel(), Level.INFO );
        assertEquals(policy.getResourceBundleName(), "Trace_Messages.properties");
    }

}
