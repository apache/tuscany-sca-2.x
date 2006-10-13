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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * This class encapsulates the configuration settings for the "XML From XSD" generation.  
 * Presently it contains settings like the XSD to be use, the schematype for which and
 * xml instance is to be created, the destination for the output etc.  All of these 
 * settings are used by the 'generator' classes during the genation of the xml instance.
 * 
 */
public class XMLfromXSDConfiguration implements CmdLineArgsHandler {
    private static int fileCount = 0;

    public static final String DEFAULT_XML_OUTPUT_FILENAME = "XML_FROM_XSD_" + (++fileCount) + ".xml";

    public static final String XSD_FILE_URL_OPTION = "xsd";

    public static final String XML_OUTPUT_LOCATION_OPTION = "o";

    public static final String XML_OUTPUT_FILENAME_OPTION = "of";

    public static final String ROOT_ELEMENT_NAMESPACE_URI_OPTION = "rns";

    public static final String ROOT_ELEMENT_LOCALNAME_OPTION = "rn";

    public static final String SCHEMA_TYPE_NAME = "st";

    public static final String SCHEMA_TYPE_NAMESPACE_URI = "stn";

    public static final String GENERATE_SAMPLE_DATA = "sd";

    protected String xmlOutputLocation = null;

    protected File xmlOutputDirectory = null;

    protected String xsdFileName = null;

    protected String xmlFileName = null;

    protected InputStream xsdInputStream = null;

    protected OutputStream xmlOutputStream = null;

    protected String rootElementNamespaceURI = null;

    protected String rootElementLocalName = null;

    protected String schemaTypeName = null;

    protected String schemaTypeNamespaceURI = null;

    protected boolean generateSampleData = false;

    private void handleXSDInputFileOption(String xsdFileUrl) throws Exception {
        xsdFileName = xsdFileUrl;
        // xsdInputStream = new FileInputStream(new File(xsdFileName));
        URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource(xsdFileName);
        if (resourceUrl != null) {
            xsdInputStream = resourceUrl.openStream();
        } else {
            File inFile = new File(xsdFileName);
            xsdFileName = inFile.getName();
            xsdInputStream = new FileInputStream(inFile);
        }
    }

    private void handleXMLOutputLocationOption(String outputLocation) throws Exception {
        xmlOutputLocation = outputLocation;
        xmlOutputDirectory = new File(xmlOutputLocation);
        if (!xmlOutputDirectory.exists()) {
            xmlOutputDirectory.mkdirs();
        } else if (!xmlOutputDirectory.isDirectory()) {
            throw new IllegalArgumentException("The input location for the xml output " + outputLocation + "is not a folder");
        }
    }

    private void handleOutputFilenameOption(String outputFileName) throws Exception {
        xmlFileName = outputFileName;

        // first create a file in the given location
        File outputFile = new File(xmlOutputDirectory, xmlFileName);

        try {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            xmlOutputStream = new FileOutputStream(outputFile);
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    public void handleArgument(String optionFlag, String argValue) throws IllegalArgumentException {
        try {
            if (XSD_FILE_URL_OPTION.equalsIgnoreCase(optionFlag)) {
                handleXSDInputFileOption(argValue);
            } else if (XML_OUTPUT_LOCATION_OPTION.equalsIgnoreCase(optionFlag)) {
                handleXMLOutputLocationOption(argValue);
            } else if (XML_OUTPUT_FILENAME_OPTION.equalsIgnoreCase(optionFlag)) {
                handleOutputFilenameOption(argValue);
            } else if (ROOT_ELEMENT_NAMESPACE_URI_OPTION.equalsIgnoreCase(optionFlag)) {
                setRootElementNamespaceURI(argValue);
            } else if (ROOT_ELEMENT_LOCALNAME_OPTION.equalsIgnoreCase(optionFlag)) {
                setRootElementLocalName(argValue);
            } else if (SCHEMA_TYPE_NAME.equalsIgnoreCase(optionFlag)) {
                setSchemaTypeName(argValue);
            } else if (SCHEMA_TYPE_NAMESPACE_URI.equalsIgnoreCase(optionFlag)) {
                setSchemaTypeNamespaceURI(argValue);
            } else if (GENERATE_SAMPLE_DATA.equalsIgnoreCase(optionFlag)) {
                setGenerateSampleData(true);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception due to - " + e);
        }
    }

    public String getRootElementLocalName() {
        if (rootElementLocalName == null) {
            rootElementLocalName = schemaTypeName.toLowerCase();
        }
        return rootElementLocalName;
    }

    public void setRootElementLocalName(String rootElementLocalName) {
        this.rootElementLocalName = rootElementLocalName;
    }

    public String getRootElementNamespaceURI() {
        if (rootElementNamespaceURI == null) {
            rootElementNamespaceURI = schemaTypeNamespaceURI;
        }
        return rootElementNamespaceURI;
    }

    public void setRootElementNamespaceURI(String rootElementNamespaceURI) {
        this.rootElementNamespaceURI = rootElementNamespaceURI;
    }

    public String getXmlFileName() {
        if (xmlFileName == null) {
            xmlFileName = getXsdFileName() + ".xml";
        }
        return xmlFileName;
    }

    public void setXmlFileName(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }

    public File getXmlOutputLocation() {
        return xmlOutputDirectory;
    }

    public void setXmlOutputLocation(File xmlOutputLocation) {
        this.xmlOutputDirectory = xmlOutputLocation;
    }

    public OutputStream getXmlOutputStream() throws Exception {
        if (xmlOutputStream == null) {
            if (xmlOutputDirectory == null) {
                handleXMLOutputLocationOption(System.getProperty("user.dir"));
            }
            handleOutputFilenameOption(getXmlFileName());
        }
        return xmlOutputStream;
    }

    public void setXmlOutputStream(OutputStream xmlOutputStream) {
        this.xmlOutputStream = xmlOutputStream;
    }

    public String getXsdFileName() {
        return xsdFileName;
    }

    public void setXsdFileName(String xsdFileName) throws Exception {
        this.xsdFileName = xsdFileName;
        handleXSDInputFileOption(xsdFileName);
    }

    public InputStream getXsdInputStream() throws Exception {
        if (xsdInputStream == null) {
            throw new IllegalArgumentException("XSD Input Source not set....!");
        }
        return xsdInputStream;
    }

    public void setXsdInputStream(InputStream xsdInputStream) {
        this.xsdInputStream = xsdInputStream;
    }

    public String getSchemaTypeName() {
        return schemaTypeName;
    }

    public void setSchemaTypeName(String schemaTypeName) {
        this.schemaTypeName = schemaTypeName;
    }

    public String getSchemaTypeNamespaceURI() {
        return schemaTypeNamespaceURI;
    }

    public void setSchemaTypeNamespaceURI(String schemaTypeNamespaceURI) {
        this.schemaTypeNamespaceURI = schemaTypeNamespaceURI;
    }

    public void setXmlOutputLocation(String xmlOutputLocation) throws Exception {
        this.xmlOutputLocation = xmlOutputLocation;
        handleXMLOutputLocationOption(xmlOutputLocation);
    }

    public boolean isGenerateSampleData() {
        return generateSampleData;
    }

    public void setGenerateSampleData(boolean generateSampleData) {
        this.generateSampleData = generateSampleData;
    }

}
