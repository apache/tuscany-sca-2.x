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

package org.apache.tuscany.sca.core.assembly.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.tuscany.sca.xsd.xml.XSDModelResolver;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.xml.sax.InputSource;

public class WSDLHelper {
    
    /**
     * This creates a WSDLInterfaceContract from a WSDL document
     * TODO: Presently this writes the wsdl string to a temporary file which is then used by the Tuscany contribution
     * code to turn the wsdl into the correctly populated Tuscany model objects. There must/should be a way to have
     * that happen without needing the external file but i've not been able to find the correct configuration to 
     * get that to happen with all the schema objects created correctly. 
     */
/*
    public static WSDLInterfaceContract createWSDLInterfaceContractViaFile(ExtensionPointRegistry registry, String wsdl) {
        File wsdlFile = null;
        try {
            
            wsdlFile = writeToFile(wsdl);
            System.out.println("wsdl: " + wsdlFile);

            FactoryExtensionPoint fep = registry.getExtensionPoint(FactoryExtensionPoint.class);
            URLArtifactProcessorExtensionPoint apep = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
            ExtensibleURLArtifactProcessor aproc = new ExtensibleURLArtifactProcessor(apep);
            ProcessorContext ctx = new ProcessorContext();
            
            ContributionFactory cf = fep.getFactory(ContributionFactory.class);
            final Contribution c = cf.createContribution();
            c.setURI("temp");
            c.setLocation(wsdlFile.toURI().toURL().toString());
            c.setModelResolver(new ExtensibleModelResolver(c, registry.getExtensionPoint(ModelResolverExtensionPoint.class), fep));
            
            WSDLDefinition wd = aproc.read(null, new URI("temp.wsdl"), wsdlFile.toURI().toURL(), ctx, WSDLDefinition.class);
            c.getModelResolver().addModel(wd, ctx);
            c.getModelResolver().resolveModel(WSDLDefinition.class, wd, ctx);
            PortType pt = (PortType)wd.getDefinition().getAllPortTypes().values().iterator().next();
            
            WSDLFactory wsdlFactory = registry.getExtensionPoint(FactoryExtensionPoint.class).getFactory(WSDLFactory.class);
            WSDLInterface nwi = wsdlFactory.createWSDLInterface(pt, wd, c.getModelResolver(), null);
            nwi.setWsdlDefinition(wd);
            WSDLInterfaceContract wsdlIC = wsdlFactory.createWSDLInterfaceContract();
            wsdlIC.setInterface(nwi);
            
            wsdlFile.delete();
            
            return wsdlIC;

//        } catch (InvalidWSDLException e) {
//            //* TODO: Also, this doesn't seem to work reliably and sometimes the schema objects don't get built correctly
//            //* org.apache.tuscany.sca.interfacedef.wsdl.impl.InvalidWSDLException: Element cannot be resolved: {http://sample/}sayHello
//            //*         at org.apache.tuscany.sca.interfacedef.wsdl.impl.WSDLOperationIntrospectorImpl$WSDLPart.<init>(WSDLOperationIntrospectorImpl.java:276)
//            //* It seems like it works ok for me with IBM JDK but not with a Sun one        
//            // I'm still trying to track this down but committing like this to see if anyone has any ideas 
//            e.printStackTrace();
//            return null;
            
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (wsdlFile != null) {
                wsdlFile.delete();
            }
        }
    }
    
    private static File writeToFile(String wsdl) throws FileNotFoundException, IOException {
        File f = File.createTempFile("endpoint", ".wsdl");
        Writer out = new OutputStreamWriter(new FileOutputStream(f));
        try {
          out.write(wsdl);
        }
        finally {
          out.close();
        }
        return f;
    }
*/
    
    /*
     * A rework of the above code that 
     *
     * 1 - doesn't use a intermediate file
     * 2 - doesn't use the Tuscany contribution code
     * 3 - takes care of imports/includes
     * 4 - takes care of call and callback interfaces 
     * 
     * Re. point 1 - In theory it's neater but the Tuscany processors/resolvers don't know how to do this
     *      so there is quite a bit of code here. I don't really like it but we can sleep on it
     *      and look at how to integrate it into the runtime or even take a different approach to
     *      moving the interface about 
     */
    public static WSDLInterfaceContract createWSDLInterfaceContract(ExtensionPointRegistry registry, String wsdl, String wsdlCallback) {
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory wsdlFactory = modelFactories.getFactory(org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory.class);
        
        WSDLInterfaceContract wsdlInterfaceContract = wsdlFactory.createWSDLInterfaceContract();
        wsdlInterfaceContract.setInterface(createWSDLInterface(registry, wsdl));
        if (wsdlCallback != null && wsdlCallback.length() > 0){
            wsdlInterfaceContract.setCallbackInterface(createWSDLInterface(registry, wsdlCallback));
        }
        
        return wsdlInterfaceContract;
    }

    /**
     * Read a single WSDL interface and it's associated XSD from a string
     * 
     * @param registry
     * @param wsdl
     * @return
     */
    public static WSDLInterface createWSDLInterface(ExtensionPointRegistry registry, String wsdl) {
        try {
            // Read all the WSDL and XSD in from the wsdl string. The WSDL and XSD appear sequentially in
            // the following format:
            //
            // filename
            // _X_
            // wsdl xml
            // _X_
            // xsd xml
            // _X_
            // xsd xml
            //
            // So we need to read each WSDL and XSD separately and then fix up the includes/imports as appropriate
            String xmlArray[] = wsdl.split("_X_");
            
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
            
            FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
            org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory wsdlFactory = modelFactories.getFactory(org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory.class);
            XSDFactory xsdFactory = modelFactories.getFactory(XSDFactory.class);
            XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
            schemaCollection.setSchemaResolver(new XSDURIResolverImpl(xmlMap));
            ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
            final org.apache.tuscany.sca.contribution.Contribution contribution = contributionFactory.createContribution();
            ProcessorContext processorContext = new ProcessorContext();
            
            ExtensibleModelResolver extensibleResolver = new ExtensibleModelResolver(contribution, registry.getExtensionPoint(ModelResolverExtensionPoint.class), modelFactories);
            ModelResolver wsdlResolver = (ModelResolver)extensibleResolver.getModelResolverInstance(WSDLDefinition.class);
            XSDModelResolver xsdResolver = (XSDModelResolver)extensibleResolver.getModelResolverInstance(XSDefinition.class);
            contribution.setURI("temp");
            contribution.setLocation(topWSDLLocation);
            contribution.setModelResolver(extensibleResolver);
    
            // read
            for (XMLString xmlString : xmlMap.values()){
                if (xmlString instanceof WSDLInfo){
                    WSDLReader reader;
                    try {
                        reader =  AccessController.doPrivileged(new PrivilegedExceptionAction<WSDLReader>() {
                            public WSDLReader run() throws WSDLException {
                                return javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader();                         
                            }
                        });
                    } catch (PrivilegedActionException e){
                        throw (WSDLException)e.getException();
                    }
                    reader.setFeature("javax.wsdl.verbose", false);
                    reader.setFeature("javax.wsdl.importDocuments", true);
                    final WSDLLocatorImpl locator = new WSDLLocatorImpl(xmlString.getBaseURI(), xmlMap);
                    final WSDLReader freader = reader;
                    Definition readDefinition;
                    try {
                        readDefinition = AccessController.doPrivileged(new PrivilegedExceptionAction<Definition>() {
                            public Definition run() throws WSDLException {
                                return freader.readWSDL(locator);                        
                            }
                        });
                    } catch (PrivilegedActionException e){
                        throw (WSDLException)e.getException();
                    }
                    
                    WSDLDefinition wsdlDefinition = wsdlFactory.createWSDLDefinition();
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
                   WSDLDefinition wsdlDefinition = ((WSDLInfo)xmlString).getWsdlDefintion();
                   
                   // link to imports
                   for (Map.Entry<String, List<javax.wsdl.Import>> entry :
                       ((Map<String, List<javax.wsdl.Import>>)wsdlDefinition.getDefinition().getImports()).entrySet()) {
                       for (javax.wsdl.Import imp : entry.getValue()) {
                           String wsdlName = imp.getDefinition().getDocumentBaseURI();
                           WSDLInfo wsdlInfo = (WSDLInfo)xmlMap.get(getFilenameWithoutPath(wsdlName));
                           wsdlDefinition.getImportedDefinitions().add(wsdlInfo.getWsdlDefintion());
                       }
                   }
                   
                   // extract any in-line types in the Tuscany model
                   Types types = wsdlDefinition.getDefinition().getTypes();
                   if ( types != null){
                       for (int i=0; i < types.getExtensibilityElements().size(); i++){
                           String schemaName = xmlString.getBaseURI() + "#" + i++;
                           XSDInfo xsdInfo = (XSDInfo)xmlMap.get(getFilenameWithoutPath(schemaName));
                           if (xsdInfo != null){
                               wsdlDefinition.getXmlSchemas().add(xsdInfo.getXsdDefinition());
                           }
                       }
                   }
                } else {
                    // Schema should already be linked via the schema model
                }
            }
            
            WSDLInfo topWSDL = (WSDLInfo)xmlMap.get(topWSDLLocation);
            WSDLDefinition topWSDLDefinition = topWSDL.getWsdlDefintion();
            
            PortType portType = (PortType)topWSDLDefinition.getDefinition().getAllPortTypes().values().iterator().next();
            WSDLInterface readWSDLInterface = wsdlFactory.createWSDLInterface(portType, topWSDLDefinition, extensibleResolver, null);        
            
            return readWSDLInterface;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /*
     * WSDL is provided in the following string form:
     * 
     * _X_
     * the_original_path_to_a_wsdl_file
     * _X_
     * the WSDL XML
     * _X_ 
     * the_original_path_to_a_related_xsd_file
     * _X_
     * the XSD XML
     * etc. 
     *
     * This structure, and the classes that specialize it, represent this format in memory
     */
    private static class XMLString {
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
    
    private static class XSDInfo extends XMLString {
        
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
    
    private static class WSDLInfo extends XMLString {
        
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

    /*
     * A WSDL locator used to find WSDL in memory based on the map
     * of all WSDL/XSD that have been read from the input string
     */
    private static class WSDLLocatorImpl implements WSDLLocator {
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
            return getInputSource(getFilenameWithoutPath(baseURI), xmlMap);
        }

        public String getBaseURI() {
            return baseURI;
        }

        public InputSource getImportInputSource(String parentLocation, String importLocation) {
            latestImportURI = importLocation;
            return getInputSource(getFilenameWithoutPath(importLocation), xmlMap);
        }

        public String getLatestImportURI() {
            return latestImportURI;
        }
    }
    
    /*
     * A local URIResolver used to find XSD in memory based on the map
     * of all WSDL/XSD that have been read from the input string
     */
    private static class XSDURIResolverImpl extends DefaultURIResolver {
        
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
            return getInputSource(getFilenameWithoutPath(schemaLocation), xmlMap);
        }
    }
    
    /*
     * Retrieve the input source for the given URI
     */
    private static InputSource getInputSource(String uri, Map<String, XMLString> xmlMap){
        String xmlString = xmlMap.get(uri).getXmlString();
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
        InputSource inputSource = new InputSource(inputStream);
        inputSource.setSystemId(uri);
        return inputSource;
    }    
    
    /*
     * Remove path from filename so that XSD/WSDL data can be found in memory
     * rather than on the remote file system
     */
    private static String getFilenameWithoutPath(String filename){
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
