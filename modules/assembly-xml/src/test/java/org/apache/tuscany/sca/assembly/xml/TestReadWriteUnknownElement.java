package org.apache.tuscany.sca.assembly.xml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.junit.Test;

public class TestReadWriteUnknownElement extends TestCase {
	
	private XMLInputFactory inputFactory;
	String XML = "<?xml version='1.0' encoding='UTF-8'?><composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:ns1=\"http://www.osoa.org/xmlns/sca/1.0\" targetNamespace=\"http://calc\" name=\"Calculator\"><service name=\"CalculatorService\" promote=\"CalculatorServiceComponent\" /><component name=\"CalculatorServiceComponent\"><reference name=\"addService\" target=\"AddServiceComponent\" /><reference name=\"subtractService\" target=\"SubtractServiceComponent\" /><reference name=\"multiplyService\" target=\"MultiplyServiceComponent\" /><reference name=\"divideService\" target=\"DivideServiceComponent\" /></component><component name=\"AddServiceComponent\" /><component name=\"SubtractServiceComponent\" /><component name=\"MultiplyServiceComponent\" /><component name=\"DivideServiceComponent\" /><x:unknownElement xmlns:x=\"http://x\" uknAttr=\"attribute1\"><x:subUnknownElement1 uknAttr1=\"attribute1\" /><x:subUnknownElement2 /></x:unknownElement></composite>";
	private ExtensibleStAXArtifactProcessor staxProcessor;
	
	 @Override
	    public void setUp() throws Exception {
	        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
	        inputFactory = XMLInputFactory.newInstance();
	        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
	        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance(), null);
	    }
	 
	 @Override
	    public void tearDown() throws Exception {
	    }
		
	@Test
	public void testReadWriteComposite() throws Exception{
		InputStream is = getClass().getResourceAsStream("Calculator.composite");
		XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
		Composite composite = (Composite)staxProcessor.read(reader);
		assertNotNull(composite);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		staxProcessor.write(composite, bos);
		System.out.println(bos.toString());
		assertEquals(XML,bos.toString());
		bos.close();
        
        is.close();
       }

}
