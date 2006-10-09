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
package org.apache.tuscany.container.javascript.utils.xmlfromxsd;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.xsd2inst.SchemaInstanceGenerator;
import org.xml.sax.InputSource;

import com.ibm.wsdl.util.xml.DOM2Writer;

public class XBbasedXMLGenerator extends SchemaInstanceGenerator implements XMLGenerator {
    private XMLfromXSDConfiguration config = null;

    public static final String QNAME_SEPARATOR = "#";

    public XBbasedXMLGenerator(XMLfromXSDConfiguration config) {
        this.config = config;
        // config.setXsdInputStream(getClass().getResource(conf).openStream());
    }

    public Hashtable<String, XmlObject> generateXmlAll() throws Exception {
        Hashtable<String, XmlObject> xmlInstances = new Hashtable<String, XmlObject>();
        TuscanySampleXmlUtil xmlUtil = new TuscanySampleXmlUtil();
        xmlUtil.setGenerate_sample_data(config.isGenerateSampleData());
        SchemaTypeSystem sts = processXSDSources();
        SchemaType elementType = null;
        for (SchemaGlobalElement globalElement : sts.globalElements()) {
            elementType = getRootElementSchemaType(sts, globalElement.getName().getNamespaceURI(), globalElement.getName().getLocalPart());
            xmlInstances.put(makeQName(globalElement.getName()), XmlObject.Factory.parse(xmlUtil.createSampleForType(elementType)));
        }

        return xmlInstances;
    }

    public String generateXMLAsString() throws Exception {
        SchemaTypeSystem sts = processXSDSources();
        SchemaType rootElementType = getRootElementSchemaType(sts, config.getSchemaTypeNamespaceURI(), config.getSchemaTypeName());
        String result = "";
        if (rootElementType != null) {
            TuscanySampleXmlUtil xmlUtil = new TuscanySampleXmlUtil();
            xmlUtil.setGenerate_sample_data(config.isGenerateSampleData());
            result = xmlUtil.createSampleForType(rootElementType);
        } else {
            System.out.println("Could not find a global element with name \"" + config.getRootElementLocalName() + "\"");
        }
        return result;
    }

    public void generateXMLIntoOutputStream() throws Exception {
        config.getXmlOutputStream().write(generateXMLAsString().getBytes());
    }

    public void generateXML() throws Exception {
        SchemaTypeSystem sts = processXSDSources();

        SchemaType rootElementType = getRootElementSchemaType(sts, config.getSchemaTypeNamespaceURI(), config.getSchemaTypeName());

        if (rootElementType != null) {
            TuscanySampleXmlUtil xmlUtil = new TuscanySampleXmlUtil();
            xmlUtil.setGenerate_sample_data(config.isGenerateSampleData());
            String result = xmlUtil.createSampleForType(rootElementType);
            config.getXmlOutputStream().write(result.getBytes());
            // System.out.println(result);
        } else {
            System.out.println("Could not find a global element with name \"" + config.getRootElementLocalName() + "\"");
        }
    }

    protected SchemaType getRootElementSchemaType(SchemaTypeSystem sts, String schemaNamespace, String schemaTypeName) {
        SchemaType schemaType = null;

        if (sts == null) {
            System.out.println("No Schemas to process.");
        } else {
            // first check in the global types
            SchemaType[] globalTypes = sts.globalTypes();
            for (int i = 0; i < globalTypes.length; i++) {
                if (schemaNamespace.equals(globalTypes[i].getName().getNamespaceURI())
                        && schemaTypeName.equals(globalTypes[i].getName().getLocalPart())) {
                    schemaType = globalTypes[i];
                    break;
                }
            }

            // next check for anonymous types defined inline within elements
            if (schemaType == null) {
                SchemaType[] globalElems = sts.documentTypes();
                for (int i = 0; i < globalElems.length; i++) {
                    if (schemaNamespace.equals(globalElems[i].getDocumentElementName().getNamespaceURI())
                            && schemaTypeName.equals(globalElems[i].getDocumentElementName().getLocalPart())) {
                        schemaType = globalElems[i];
                        break;
                    }
                }
            }
        }
        return schemaType;
    }

    public void generateXML(XMLfromXSDConfiguration config) throws Exception {
        this.config = config;
        generateXML();
    }

    public XMLfromXSDConfiguration getConfig() {
        return config;
    }

    public void setConfig(XMLfromXSDConfiguration config) {
        this.config = config;
    }

    private Definition readInTheWSDLFile(InputStream wsdlStream) throws WSDLException {
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("javax.wsdl.importDocuments", true);

        /*
         * File file = new File(uri); String baseURI;
         * 
         * if (uri.startsWith("http://")){ baseURI = uri; } else{ if(file.getParentFile() == null){ try { baseURI = new
         * File(".").getCanonicalFile().toURI().toString(); } catch (IOException e) { throw new RuntimeException(e); } } else { baseURI =
         * file.getParentFile().toURI().toString(); } }
         */
        // Document doc;
        InputSource inputSource;
        try {

            // doc = XMLUtils.newDocument(wsdlStream);
            inputSource = new InputSource(wsdlStream);
            /*
             * } catch (ParserConfigurationException e) { throw new WSDLException(WSDLException.PARSER_ERROR, "Parser Configuration Error", e); }
             * catch (SAXException e) { throw new WSDLException(WSDLException.PARSER_ERROR, "Parser SAX Error", e);
             */
        } catch (Exception e) {
            throw new WSDLException(WSDLException.INVALID_WSDL, "IO Error", e);
        }

        // return reader.readWSDL(config.getRootElementNamespaceURI(), doc);
        return reader.readWSDL(config.getRootElementNamespaceURI(), inputSource);
    }

    protected InputStream[] getXSDsFromWSDL(InputStream xsdInputStream) throws Exception {
        Definition defn = readInTheWSDLFile(xsdInputStream);
        List list = defn.getTypes().getExtensibilityElements();

        InputStream[] xsdFragments = new InputStream[list.size()];

        Iterator iterator = list.iterator();
        Schema schemaElement = null;

        for (int count = 0; count < list.size(); ++count) {
            schemaElement = (Schema) iterator.next();
            // System.out.println(" *** - " + element.getElementType().getNamespaceURI());
            // System.out.println(" **** - " + element + " & " + element.getClass().getPackage().getName());
            // System.out.println(DOM2Writer.nodeToString(element.getElement()));
            xsdFragments[count] = new ByteArrayInputStream(DOM2Writer.nodeToString(schemaElement.getElement()).getBytes());
        }

        return xsdFragments;
    }

    protected SchemaTypeSystem processXSDSources() throws Exception {
        SchemaTypeSystem sts = null;
        if (config.getXsdFileName().endsWith(XSD_FILE)) {
            sts = processXSDs(new InputStream[] { config.getXsdInputStream() });
        } else if (config.getXsdFileName().endsWith(WSDL_FILE)) {
            sts = processXSDs(getXSDsFromWSDL(config.getXsdInputStream()));
        }
        return sts;
    }

    protected SchemaTypeSystem processXSDs(InputStream[] inputStreams) {
        List sdocs = new ArrayList();
        for (int i = 0; i < inputStreams.length; i++) {
            try {
                sdocs.add(XmlObject.Factory.parse(inputStreams[i], (new XmlOptions()).setLoadLineNumbers().setLoadMessageDigest()));
            } catch (Exception e) {
                System.err.println("Can not load schema file: " + inputStreams[i] + ": ");
                e.printStackTrace();
            }
        }

        XmlObject[] schemas = (XmlObject[]) sdocs.toArray(new XmlObject[sdocs.size()]);

        SchemaTypeSystem sts = null;
        if (schemas.length > 0) {
            Collection errors = new ArrayList();
            XmlOptions compileOptions = new XmlOptions();
            /*
             * if (dl) compileOptions.setCompileDownloadUrls(); if (nopvr) compileOptions.setCompileNoPvrRule(); if (noupa)
             * compileOptions.setCompileNoUpaRule();
             */
            try {
                sts = XmlBeans.compileXsd(schemas, XmlBeans.getBuiltinTypeSystem(), compileOptions);
            } catch (Exception e) {
                if (errors.isEmpty() || !(e instanceof XmlException))
                    e.printStackTrace();

                System.out.println("Schema compilation errors: ");
                for (Iterator i = errors.iterator(); i.hasNext();)
                    System.out.println(i.next());
            }
        }

        return sts;
    }

    private String makeQName(String nameSpace, String localName) {
        return nameSpace + QNAME_SEPARATOR + localName;
    }

    private String makeQName(QName qName) {
        return qName.getNamespaceURI() + QNAME_SEPARATOR + qName.getLocalPart();
    }

}
