package org.apache.tuscany.binding.jms;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.spi.loader.LoaderException;
import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

public class JMSBindingLoaderTestCase extends TestCase {

    private JMSBindingLoader loader;

    public void testMinimal() throws LoaderException, XMLStreamException {
        String xml = "<binding.jms></binding.jms>";
        XMLStreamReader reader = createReader(xml);
        JMSBindingDefinition jmsBinding = loader.load(null, null, reader, null);
        assertNotNull(jmsBinding);
    }

    public void testCorrelationScheme() throws LoaderException, XMLStreamException {
        XMLStreamReader reader =
            createReader("<binding.jms correlationScheme=\"RequestMsgIDToCorrelID\"></binding.jms>");
        JMSBindingDefinition jmsBinding = loader.load(null, null, reader, null);
        assertEquals("RequestMsgIDToCorrelID", jmsBinding.getCorrelationScheme());

        reader = createReader("<binding.jms correlationScheme=\"RequestCorrelIDToCorrelID\"></binding.jms>");
        jmsBinding = loader.load(null, null, reader, null);
        assertEquals("RequestCorrelIDToCorrelID", jmsBinding.getCorrelationScheme());

        reader = createReader("<binding.jms correlationScheme=\"none\"></binding.jms>");
        jmsBinding = loader.load(null, null, reader, null);
        assertEquals("none", jmsBinding.getCorrelationScheme());

        reader = createReader("<binding.jms correlationScheme=\"xxx\"></binding.jms>");
        try {
            jmsBinding = loader.load(null, null, reader, null);
            fail("expecting invalid correlationScheme");
        } catch (LoaderException e) {
            // expected
        }
    }

    public void testDestination() throws LoaderException, XMLStreamException {
        XMLStreamReader reader = createReader("<binding.jms><destination name=\"foo\"/></binding.jms>");
        JMSBindingDefinition jmsBinding = loader.load(null, null, reader, null);
        assertEquals("foo", jmsBinding.getDestinationName());
    }

    public void testInitialContextFactory() throws LoaderException, XMLStreamException {
        XMLStreamReader reader = createReader("<binding.jms initialContextFactory=\"myicf\"></binding.jms>");
        JMSBindingDefinition jmsBinding = loader.load(null, null, reader, null);
        assertEquals("myicf", jmsBinding.getInitialContextFactoryName());
    }

    public void testJNDIProviderURL() throws LoaderException, XMLStreamException {
        XMLStreamReader reader = createReader("<binding.jms JNDIProviderURL=\"myURL\"></binding.jms>");
        JMSBindingDefinition jmsBinding = loader.load(null, null, reader, null);
        assertEquals("myURL", jmsBinding.getJNDIProviderURL());
    }

    public void testConnectionFactory() throws LoaderException, XMLStreamException {
        XMLStreamReader reader = createReader("<binding.jms> <connectionFactory name=\"myfactory\"/> </binding.jms>");
        JMSBindingDefinition jmsBinding = loader.load(null, null, reader, null);
        assertEquals("myfactory", jmsBinding.getConnectionFactoryName());
    }

    public void testActivationSpec() throws LoaderException, XMLStreamException {
        XMLStreamReader reader = createReader("<binding.jms> <activationSpec name=\"myas\"/></binding.jms>");
        JMSBindingDefinition jmsBinding = loader.load(null, null, reader, null);
        assertEquals("myas", jmsBinding.getActivationSpecName());
    }

    private XMLStreamReader createReader(String xml) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();

        String xxx = "<xxx xmlns=\"" + XML_NAMESPACE_1_0 + "\">" + xml + "</xxx>";
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xxx));
        reader.nextTag();
        reader.nextTag();
        return reader;
    }

    protected void setUp() throws Exception {
        this.loader = new JMSBindingLoader(null);
    }
}
