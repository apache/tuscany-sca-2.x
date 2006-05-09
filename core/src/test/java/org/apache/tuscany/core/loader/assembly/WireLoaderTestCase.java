package org.apache.tuscany.core.loader.assembly;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.model.assembly.Wire;

/**
 * @version $Rev$ $Date$
 */
public class WireLoaderTestCase extends LoaderTestSupport {

    public void testMinimal() throws XMLStreamException, ConfigurationLoadException {
        String xml = "<wire xmlns='http://www.osoa.org/xmlns/sca/0.9'><source.uri>foo/fooService</source.uri><target.uri>bar</target.uri></wire>";
        XMLStreamReader reader = getReader(xml);
        Wire wire = (Wire) registry.load(reader, loaderContext);
        reader.require(XMLStreamConstants.END_ELEMENT, AssemblyConstants.WIRE.getNamespaceURI(), AssemblyConstants.WIRE.getLocalPart());
        assertEquals(XMLStreamConstants.END_DOCUMENT, reader.next());
        assertNotNull(wire);
        assertEquals("foo", wire.getSource().getPartName());
        assertEquals("fooService", wire.getSource().getServiceName());
        assertEquals("bar", wire.getTarget().getPartName());
    }

    public void testCompound() throws XMLStreamException, ConfigurationLoadException {
        String xml = "<wire xmlns='http://www.osoa.org/xmlns/sca/0.9'><source.uri>foo/fooService</source.uri><target.uri>bar/bazService</target.uri></wire>";
        XMLStreamReader reader = getReader(xml);
        Wire wire = (Wire) registry.load(reader, loaderContext);
        reader.require(XMLStreamConstants.END_ELEMENT, AssemblyConstants.WIRE.getNamespaceURI(), AssemblyConstants.WIRE.getLocalPart());
        assertEquals(XMLStreamConstants.END_DOCUMENT, reader.next());
        assertNotNull(wire);
        assertEquals("foo", wire.getSource().getPartName());
        assertEquals("fooService", wire.getSource().getServiceName());
        assertEquals("bar", wire.getTarget().getPartName());
        assertEquals("bazService", wire.getTarget().getServiceName());
    }

    protected void setUp() throws Exception {
        super.setUp();
        registerLoader(new WireLoader());
    }
}
