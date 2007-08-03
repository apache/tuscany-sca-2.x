package org.apache.tuscany.sca.contribution.java.impl;


import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.java.JavaExport;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;

/**
 * Test JavaExportProcessorTestCase
 * 
 * @version $Rev$ $Date$
 */
public class JavaExportProcessorTestCase extends TestCase {

    private static final String VALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<export.java package=\"org.apache.tuscany.sca.contribution.java\"/>"
            + "</contribution>";

    private static final String INVALID_XML =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<contribution xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns=\"http://ns\">"
            + "<export.java/>"
            + "</contribution>";

    private XMLInputFactory xmlFactory;

    protected void setUp() throws Exception {
        super.setUp();
        xmlFactory = XMLInputFactory.newInstance();
    }

    /**
     * Test loading a valid export element from a contribution metadata stream
     * @throws Exception
     */
    public void testLoad() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(VALID_XML));

        JavaExportProcessor exportProcessor = new JavaExportProcessor(new JavaImportExportFactoryImpl());
        JavaExport javaExport = exportProcessor.read(reader);
        
        assertEquals("org.apache.tuscany.sca.contribution.java", javaExport.getPackage());
    }

    /**
     * Test loading an INVALID export element from a contribution metadata stream
     * @throws Exception
     */
    public void testLoadInvalid() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(INVALID_XML));

        JavaExportProcessor exportProcessor = new JavaExportProcessor(new JavaImportExportFactoryImpl());
        try {
            exportProcessor.read(reader);
            fail("readerException should have been thrown");
        } catch (ContributionReadException e) {
            assertTrue(true);
        }
    }    
}
