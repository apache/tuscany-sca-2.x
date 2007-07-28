package org.apache.tuscany.sca.contribution.java.impl;


import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.impl.JavaImportProcessor;

/**
 * @version $Rev: 538445 $ $Date: 2007-05-15 23:20:37 -0700 (Tue, 15 May 2007) $
 */
public class JavaImportMetadataProcessorTestCase extends TestCase {

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

    protected void setUp() throws Exception {
        super.setUp();
        xmlFactory = XMLInputFactory.newInstance();
    }

    public void testLoad() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(VALID_XML));

        JavaImportProcessor importProcessor = new JavaImportProcessor();
        JavaImport javaImport = importProcessor.read(reader);
        
        assertEquals("org.apache.tuscany.sca.contribution.java", javaImport.getPackage());
        assertEquals("sca://contributions/001", javaImport.getLocation());
    }

    public void testLoadInvalid() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(INVALID_XML));

        JavaImportProcessor importProcessor = new JavaImportProcessor();
        try {
            importProcessor.read(reader);
            fail("readerException should have been thrown");
        } catch (ContributionReadException e) {
            assertTrue(true);
        }
    }    
}
