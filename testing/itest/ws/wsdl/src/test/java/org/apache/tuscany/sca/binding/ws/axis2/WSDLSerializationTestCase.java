/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tuscany.sca.binding.ws.axis2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;


import org.apache.tuscany.sca.common.xml.XMLDocumentHelper;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.impl.RuntimeEndpointImpl;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.WSDLModelResolver;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.tuscany.sca.xsd.xml.XSDModelResolver;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaCollectionEnumerator;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.junit.Assert;
import org.junit.Ignore;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

/**
 * Tests that WSDL can be serialized out in a form that can be passed
 * across the registry in a single shot
 */
public class WSDLSerializationTestCase extends TestCase {
    
    private static byte separator[] = {'_', 'X', '_'};

/*
    public void testWSDL4JSerialization() throws Exception {
        
        // read WSDL in 
        WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
        wsdlReader.setFeature("javax.wsdl.verbose",false);
        wsdlReader.setFeature("javax.wsdl.importDocuments",true);

        Definition definition = wsdlReader.readWSDL("target/classes/org/apache/tuscany/sca/binding/ws/axis2/wsdl-serialize-top.wsdl");
        assertNotNull(definition);
        
        // write WSDL out
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            WSDLWriter writer = WSDLFactory.newInstance().newWSDLWriter();
            writer.writeWSDL(definition, outStream);
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        //System.out.println(outStream.toString());
    }
*/    
    
    public void testTuscanySerialization() throws Exception {  
        // read in WSDL
        String contributionLocation = "target/classes";
        NodeImpl node = (NodeImpl)NodeFactory.newInstance().createNode("org/apache/tuscany/sca/binding/ws/axis2/wsdl-serialize.composite", 
                                                                       new Contribution("test", contributionLocation));
        node.start();
        
        RuntimeEndpointImpl endpoint = (RuntimeEndpointImpl)node.getDomainComposite().getComponents().get(0).getServices().get(0).getEndpoints().get(0);
        WSDLInterface wsdlInterface = (WSDLInterface)endpoint.getBindingInterfaceContract().getInterface();
        
        WSDLDefinition wsdlDefinition = wsdlInterface.getWsdlDefinition();
        Definition definition = wsdlDefinition.getDefinition();
        
        // write out a flattened WSDL along with XSD
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            WSDLWriter writer = WSDLFactory.newInstance().newWSDLWriter();
            String baseURI = wsdlDefinition.getLocation().toString();
            outStream.write(baseURI.getBytes());
            outStream.write(separator);            
            writer.writeWSDL(definition, outStream);
            for (WSDLDefinition importedWSDLDefintion : wsdlDefinition.getImportedDefinitions()){
                outStream.write(separator);
                baseURI = importedWSDLDefintion.getLocation().toString();
                outStream.write(baseURI.getBytes());
                outStream.write(separator);
                writer.writeWSDL(importedWSDLDefintion.getDefinition(), outStream);
            }
            for (XSDefinition xsdDefinition : wsdlDefinition.getXmlSchemas()){
                // we store a reference to the schema schema. We don't need to write that out.
                // also ignore schema that are extract from the original WSDL (have in their location)
                if (!xsdDefinition.getNamespace().equals("http://www.w3.org/2001/XMLSchema") &&
                    xsdDefinition.getSchema() != null){
                    writeSchema(outStream, xsdDefinition.getSchema());
                }
            }           
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        String wsdlString = outStream.toString();
        //System.out.println(wsdlString);
        
        // Read the WSDL and XSD back in from the string
        String xmlArray[] = wsdlString.split("_X_");
        String topWSDLLocation = null;
        Map<String, XMLString> xmlMap = new HashMap<String, XMLString>();
        
        for (int i = 0; i < xmlArray.length; i = i + 2){
            String location = xmlArray[i];
            String xml = xmlArray[i+1];
            // strip the file name out of the location
            location = location.substring(location.lastIndexOf("/") + 1);
            
            if (location.endsWith(".wsdl")){
                xmlMap.put(location,
                           new WSDLInfo(xmlArray[i],
                                          xml));
                
                if (topWSDLLocation == null){
                    topWSDLLocation = location;
                }
            } else {
                xmlMap.put(location,
                        new XSDInfo(xmlArray[i],
                                      xml));
            }
        }
        
        ExtensionPointRegistry registry = endpoint.getCompositeContext().getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory wsdlFactory = modelFactories.getFactory(org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory.class);
        XSDFactory xsdFactory = modelFactories.getFactory(XSDFactory.class);
        XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
        schemaCollection.setSchemaResolver(new XSDURIResolverImpl(xmlMap));
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        final org.apache.tuscany.sca.contribution.Contribution contribution = contributionFactory.createContribution();
        ProcessorContext processorContext = new ProcessorContext();
        
        ExtensibleModelResolver extensibleResolver = new ExtensibleModelResolver(contribution, registry.getExtensionPoint(ModelResolverExtensionPoint.class), modelFactories);
        WSDLModelResolver wsdlResolver = (WSDLModelResolver)extensibleResolver.getModelResolverInstance(WSDLDefinition.class);
        XSDModelResolver xsdResolver = (XSDModelResolver)extensibleResolver.getModelResolverInstance(XSDefinition.class);
        contribution.setURI("temp");
        contribution.setLocation(topWSDLLocation);
        contribution.setModelResolver(extensibleResolver);

        // read
        for (XMLString xmlString : xmlMap.values()){
            if (xmlString instanceof WSDLInfo){
                WSDLReader reader =  WSDLFactory.newInstance().newWSDLReader();
                reader.setFeature("javax.wsdl.verbose", false);
                reader.setFeature("javax.wsdl.importDocuments", true);
                WSDLLocatorImpl locator = new WSDLLocatorImpl(xmlString.getBaseURI(), xmlMap);
                Definition readDefinition = reader.readWSDL(locator);
                
                wsdlDefinition = wsdlFactory.createWSDLDefinition();
                wsdlDefinition.setDefinition(readDefinition);
                wsdlDefinition.setLocation(new URI(xmlString.getBaseURI()));
                
                ((WSDLInfo)xmlString).setWsdlDefintion(wsdlDefinition);
                wsdlResolver.addModel(wsdlDefinition, processorContext);
                
            } else {
                InputStream inputStream = new ByteArrayInputStream(xmlString.getXmlString().getBytes());
                InputSource inputSource = new InputSource(inputStream);
                inputSource.setSystemId(xmlString.getBaseURI());
                XmlSchema schema = schemaCollection.read(inputSource, null);
                inputStream.close();
                
                XSDefinition xsdDefinition = xsdFactory.createXSDefinition();
                xsdDefinition.setSchema(schema);
                
                ((XSDInfo)xmlString).setXsdDefinition(xsdDefinition);
                xsdResolver.addModel(xsdDefinition, processorContext);
            }
        }
        
        // resolve
        for (XMLString xmlString : xmlMap.values()){
            if (xmlString instanceof WSDLInfo){
               wsdlDefinition = ((WSDLInfo)xmlString).getWsdlDefintion();
               
               // link to imports
               for (Map.Entry<String, List<javax.wsdl.Import>> entry :
                   ((Map<String, List<javax.wsdl.Import>>)wsdlDefinition.getDefinition().getImports()).entrySet()) {
                   for (javax.wsdl.Import imp : entry.getValue()) {
                       String wsdlName = imp.getDefinition().getDocumentBaseURI();
                       WSDLInfo wsdlInfo = (WSDLInfo)xmlMap.get(getFilenameWithoutPath(wsdlName));
                       wsdlDefinition.getImportedDefinitions().add(wsdlInfo.getWsdlDefintion());
                   }
               }
               
               // link to in-line types
               Types types = wsdlDefinition.getDefinition().getTypes();
               if ( types != null){
                   for (int i=0; i < types.getExtensibilityElements().size(); i++){
                       String schemaName = xmlString.getBaseURI() + "#" + i++;
                       XSDInfo xsdInfo = (XSDInfo)xmlMap.get(getFilenameWithoutPath(schemaName));
                       wsdlDefinition.getXmlSchemas().add(xsdInfo.getXsdDefinition());
                   }
               }

            } else {
                
            }
        }
        
        WSDLInfo topWSDL = (WSDLInfo)xmlMap.get(topWSDLLocation);
        WSDLDefinition topWSDLDefinition = topWSDL.getWsdlDefintion();
        
        PortType portType = (PortType)topWSDLDefinition.getDefinition().getAllPortTypes().values().iterator().next();
        WSDLInterface readWSDLInterface = wsdlFactory.createWSDLInterface(portType, topWSDLDefinition, extensibleResolver, null);
        
        // now check what we have just read with what we started out with to see if the compatibility test passes
        // in the real system have to check contracts not interfaces
        UtilityExtensionPoint utils = registry.getExtensionPoint(UtilityExtensionPoint.class);
        InterfaceContractMapper interfaceContractMapper = utils.getUtility(InterfaceContractMapper.class);
        boolean match = interfaceContractMapper.isCompatibleSubset(wsdlInterface, readWSDLInterface);
        Assert.assertTrue(match);
        
        node.stop();
    }
    
    public void writeSchema(OutputStream outStream, XmlSchema schema) throws IOException {
        if (!schema.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema") &&
                schema.getNamespaceContext() != null){ 
            outStream.write(separator);
            String baseURI = schema.getSourceURI();
            outStream.write(baseURI.getBytes());
            outStream.write(separator);
            schema.write(outStream);
            
            for (Iterator<?> i = schema.getIncludes().getIterator(); i.hasNext();) {
                XmlSchemaObject obj = (XmlSchemaObject)i.next();
                XmlSchema ext = null;
                if (obj instanceof XmlSchemaInclude) {
                    ext = ((XmlSchemaInclude)obj).getSchema();
                }
                if (obj instanceof XmlSchemaImport) {
                    ext = ((XmlSchemaImport)obj).getSchema();
                }
                writeSchema(outStream, ext);
            } 
        }
    }
       
    public String calculateUniquePrefix(Map<String, String> targetNamespaces, String prefix){
        String newPrefix = prefix;
        boolean notUnique = targetNamespaces.containsKey(newPrefix);
        int i = 1;
        
        while(notUnique){
            newPrefix = prefix + i++; 
            notUnique = targetNamespaces.containsKey(newPrefix);
        }
        
        return newPrefix;
    }

    @Override
    protected void setUp() throws Exception {

    }

    @Override
    protected void tearDown() throws Exception {

    }
    
    private class XMLString {
        private String baseURI;
        private String xmlString;
        
        public XMLString(String baseURI, String xmlString){
            this.baseURI = baseURI;
            this.xmlString = xmlString;
        }
        
        public String getBaseURI() {
            return baseURI;
        }
        
        public String getXmlString() {
            return xmlString;
        }
    }
    
    private class XSDInfo extends XMLString {
        
        XSDefinition xsdDefinition;
        
        public XSDInfo(String baseURI, String xmlString) {
            super(baseURI, xmlString);
        }
        
        public void setXsdDefinition(XSDefinition xsdDefinition) {
            this.xsdDefinition = xsdDefinition;
        }
        
        public XSDefinition getXsdDefinition() {
            return xsdDefinition;
        }
    }
    
    private class WSDLInfo extends XMLString {
        
        WSDLDefinition wsdlDefintion;
        
        public WSDLInfo(String baseURI, String xmlString) {
            super(baseURI, xmlString);
        }
        
        public void setWsdlDefintion(WSDLDefinition wsdlDefintion) {
            this.wsdlDefintion = wsdlDefintion;
        }
        
        public WSDLDefinition getWsdlDefintion() {
            return wsdlDefintion;
        }
    }    

    private class WSDLLocatorImpl implements WSDLLocator {
        private Map<String, XMLString> xmlMap;
        private String baseURI;
        private String latestImportURI;

        public WSDLLocatorImpl(String baseURI, Map<String, XMLString> xmlMap) {
            this.baseURI = baseURI;
            this.xmlMap = xmlMap;
        }

        public void close() {
/*            
            try {
                inputStream.close();
            } catch (IOException e) {
                // Ignore
            }
*/            
        }

        public InputSource getBaseInputSource() {
            return getInputSource(getFilenameWithoutPath(baseURI));
        }

        public String getBaseURI() {
            return baseURI;
        }

        public InputSource getImportInputSource(String parentLocation, String importLocation) {
            latestImportURI = importLocation;
            return getInputSource(getFilenameWithoutPath(importLocation));
        }

        public String getLatestImportURI() {
            return latestImportURI;
        }
        
        private InputSource getInputSource(String uri){
            String xmlString = xmlMap.get(uri).getXmlString();
            InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
            //InputSource inputSource = XMLDocumentHelper.getInputSource(uri, inputStream);
            InputSource inputSource = new InputSource(inputStream);
            inputSource.setSystemId(uri);
            return inputSource;
        } 
    }
    
    private class XSDURIResolverImpl extends DefaultURIResolver {
        
        private Map<String, XMLString> xmlMap;
        
        public XSDURIResolverImpl(Map<String, XMLString> xmlMap) {
            this.xmlMap = xmlMap;
        }
        
        
        @Override
        protected URL getURL(URL contextURL, String spec) throws IOException {
            return super.getURL(contextURL, spec);
        }
        @Override
        public InputSource resolveEntity(String namespace,
                                         String schemaLocation, 
                                         String baseUri) {
            return getInputSource(getFilenameWithoutPath(schemaLocation));
        }
        
        private InputSource getInputSource(String uri){
            String xmlString = xmlMap.get(uri).getXmlString();
            InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
            InputSource inputSource = new InputSource(inputStream);
            inputSource.setSystemId(uri);
            return inputSource;
        }
        
    }
    
    private String getFilenameWithoutPath(String filename){
        // work out what the file name is that is being imported
        // XSDs imports are written out by Tuscany with an relative web address such as
        //  /services/AccountService?xsd=wsdl-serialize.xsd
        // for the time being just string the file name off the end. We are making
        // assumption that the interface doesn't involve two files with the same
        // name in different locations            
        int xsdIndex = filename.lastIndexOf("?xsd="); 
        int wsdlIndex = filename.lastIndexOf("/");
        if ( xsdIndex >= 0){
            return filename.substring(xsdIndex + 5);
        } else {
            return filename.substring(wsdlIndex + 1);
        }
        // What happens with generated WSDL?
    }  
}
