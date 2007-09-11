package org.apache.tuscany.sca.policy.security.ws;


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
        Axis2ConfigParamPolicyProcessor processor = new Axis2ConfigParamPolicyProcessor(null);
        
        URL url = getClass().getResource("mock_policies.xml");
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        
        InputStream urlStream = url.openStream();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(urlStream);
        
        
        Axis2ConfigParamPolicy policy = processor.read(reader);
        assertEquals(policy.getParamElements().size(), 2);
    }

}
