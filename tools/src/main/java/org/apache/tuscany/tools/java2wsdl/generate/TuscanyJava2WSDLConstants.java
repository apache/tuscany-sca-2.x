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
    
    //long options
    String IMPORT_XSD_OPTION_LONG = "import_xsd";       //option for importing XSDs
    String ATTR_FORM_DEFAULT_OPTION_LONG = "attributeFormDefault";
    String ELEMENT_FORM_DEFAULT_OPTION_LONG = "elementFormDefault";
}
