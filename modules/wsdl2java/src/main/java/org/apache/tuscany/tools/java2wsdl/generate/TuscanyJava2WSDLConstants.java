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
package org.apache.tuscany.tools.java2wsdl.generate;

import org.apache.ws.java2wsdl.Java2WSDLConstants;

/**
 * This is a extension from the Axis2 Java2WSDLConstants to handle additions specific to Tuscany.
 * This class can be done away with once Axis2 is also enhanced to support these 
 * additional options. 
 *
 */
public interface TuscanyJava2WSDLConstants extends Java2WSDLConstants 
{
    public static final char OPEN_BRACKET = '[';
    public static final char COMMA = ',';
    public static final char CLOSE_BRACKET = ']';
    public static final String DEFAULT_SCHEMA_LOCATION = "*.xsd";
    public static final String SCHEMA_ELEMENT_NAME = "schema";
    
    String FORM_DEFAULT_QUALIFIED = "qualified";
    String FORM_DEFAULT_UNQUALIFIED = "unqualified";
    
    //short options
    String IMPORT_XSD_OPTION = "ixsd";      //option for importing XSDs
    String ATTR_FORM_DEFAULT_OPTION = "afd";
    String ELEMENT_FORM_DEFAULT_OPTION = "efd";
    String EXTRA_CLASSES_DEFAULT_OPTION = "xc";
    
    //long options
    String IMPORT_XSD_OPTION_LONG = "import_xsd";       //option for importing XSDs
    String ATTR_FORM_DEFAULT_OPTION_LONG = "attributeFormDefault";
    String ELEMENT_FORM_DEFAULT_OPTION_LONG = "elementFormDefault";
    String EXTRA_CLASSES_DEFAULT_OPTION_LONG = "extraClasses";
}
