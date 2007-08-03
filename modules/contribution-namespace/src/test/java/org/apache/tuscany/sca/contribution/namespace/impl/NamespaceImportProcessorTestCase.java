package org.apache.tuscany.sca.contribution.namespace.impl;



import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;

/**
 * Test NamespaceImportProcessorTestCase
 * 
 * @version $Rev$ $Date$
 */
public class NamespaceImportProcessorTestCase extends TestCase {

    private static final String VALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<import namespace=\"http://foo\" location=\"sca://contributions/001\"/>"
            + "</contribution>";

    private static final String INVALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<import location=\"sca://contributions/001\"/>"
            + "</contribution>";

    private XMLInputFactory xmlFactory;

    protected void setUp() throws Exception {
        super.setUp();
        xmlFactory = XMLInputFactory.newInstance();
    }

    /**
     * Test loading a valid import element from a contribution metadata stream
     * @throws Exception
     */
    public void testLoad() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(VALID_XML));

        NamespaceImportProcessor importProcessor = new NamespaceImportProcessor(new NamespaceImportExportFactoryImpl());
        NamespaceImport namespaceImport = importProcessor.read(reader);
        
        assertEquals("http://foo", namespaceImport.getNamespace());
        assertEquals("sca://contributions/001", namespaceImport.getLocation());
    }

    /**
     * Test loading a INVALID import element from a contribution metadata stream
     * @throws Exception
     */
    public void testLoadInvalid() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(INVALID_XML));

        NamespaceImportProcessor importProcessor = new NamespaceImportProcessor(new NamespaceImportExportFactoryImpl());
        try {
            importProcessor.read(reader);
            fail("readerException should have been thrown");
        } catch (ContributionReadException e) {
            assertTrue(true);
        }
    }    
}
