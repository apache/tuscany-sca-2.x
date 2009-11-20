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

package org.apache.tuscany.sca.common.xml.sax;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Helper class for SAX parsing
 */
public class SAXHelper {
    private final SAXParserFactory saxParserFactory;

    /**
     * @param saxParserFactory
     */
    public SAXHelper(SAXParserFactory saxParserFactory) {
        super();
        this.saxParserFactory = saxParserFactory;
    }
    
    public SAXHelper(ExtensionPointRegistry registry) {
        FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        saxParserFactory = factories.getFactory(SAXParserFactory.class);
        saxParserFactory.setNamespaceAware(true);
    }
    
    public SAXHelper getInstance(ExtensionPointRegistry registry) {
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        return utilities.getUtility(SAXHelper.class);
    }

    public SAXParser newSAXParser() throws SAXException {
        try {
            return saxParserFactory.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public XMLReader newXMLReader() throws SAXException {
        return newSAXParser().getXMLReader();
    }

    public void parse(String xmlString, ContentHandler handler) throws SAXException, IOException {
        XMLReader reader = newXMLReader();
        reader.setContentHandler(handler);
        reader.parse(new InputSource(new StringReader(xmlString)));
    }
}
