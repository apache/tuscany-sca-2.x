package org.apache.tuscany.sca.contribution.java.impl;


import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;

/**
 * Test JavaImportProcessorTestCase
 * 
 * @version $Rev$ $Date$
 */
public class JavaImportProcessorTestCase extends TestCase {

    private static final String VALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<import.java package=\"org.apache.tuscany.sca.contribution.java\" location=\"sca://contributions/001\"/>"
            + "</contribution>";

    private static final String INVALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<import.java location=\"sca://contributions/001\"/>"
            + "</contribution>";

    private XMLInputFactory xmlFactory;

    @Override
    protected void setUp() throws Exception {
        xmlFactory = XMLInputFactory.newInstance();
    }

    /**
     * Test loading a valid import element from a contribution metadata stream
     * @throws Exception
     */
    public void testLoad() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(VALID_XML));

        ModelFactoryExtensionPoint factories = new DefaultModelFactoryExtensionPoint();
        factories.addFactory(new JavaImportExportFactoryImpl());
        JavaImportProcessor importProcessor = new JavaImportProcessor(factories);
        JavaImport javaImport = importProcessor.read(reader);
        
        assertEquals("org.apache.tuscany.sca.contribution.java", javaImport.getPackage());
        assertEquals("sca://contributions/001", javaImport.getLocation());
    }

    /**
     * Test loading a INVALID import element from a contribution metadata stream
     * @throws Exception
     */
    public void testLoadInvalid() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(INVALID_XML));

        ModelFactoryExtensionPoint factories = new DefaultModelFactoryExtensionPoint();
        factories.addFactory(new JavaImportExportFactoryImpl());
        JavaImportProcessor importProcessor = new JavaImportProcessor(factories);
        try {
            importProcessor.read(reader);
            fail("readerException should have been thrown");
        } catch (ContributionReadException e) {
            assertTrue(true);
        }
    }    
}
