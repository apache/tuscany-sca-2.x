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

import java.util.Hashtable;

import org.apache.xmlbeans.XmlObject;

/**
 * This is the XMLGenerator Interface that will be implemented by various
 * types of Generators (SDO based, XMLBeans based etc.)
 *
 */
public interface XMLGenerator {
    public static final int SDO_BASED = 1;

    public static final int XMLBEANS_BASED = 2;

    public static final String XSD_FILE = ".xsd";

    public static final String WSDL_FILE = ".wsdl";

    public void generateXML() throws Exception;

    public void generateXML(XMLfromXSDConfiguration config) throws Exception;

    public void generateXMLIntoOutputStream() throws Exception;

    public String generateXMLAsString() throws Exception;

    public Hashtable<String, XmlObject> generateXmlAll() throws Exception;

    public XMLfromXSDConfiguration getConfig();

    public void setConfig(XMLfromXSDConfiguration config);
}
