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

package org.apache.tuscany.sca.contribution.processor;

import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * 
 * A validating XMLStreamReader that reports XMLSchema validation errors.
 *
 * @version $Rev$ $Date$
 */
class ValidatingXMLStreamReader extends StreamReaderDelegate implements XMLStreamReader {

    private static final Logger logger = Logger.getLogger(ValidatingXMLStreamReader.class.getName());
    
    private ValidatorHandler handler;
    private Schema schema;
    private Monitor monitor;
    
    /**
     * Constructs a new ValidatingXMLStreamReader.
     * 
     * @param reader
     * @param schema
     * @throws XMLStreamException
     */
    ValidatingXMLStreamReader(XMLStreamReader reader, Schema schema, Monitor monitor) throws XMLStreamException {
        super(reader);
        this.monitor = monitor;
        this.schema = schema;
    }
    
    void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    private synchronized ValidatorHandler getHandler() throws XMLStreamException {
        if (schema == null || handler!=null) {
            return handler;
        }
        handler = schema.newValidatorHandler();
        handler.setDocumentLocator(new LocatorAdapter());
        try {
            handler.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        } catch (SAXException e) {
        	XMLStreamException xse = new XMLStreamException(e);
        	error("XMLStreamException", handler, xse);
            throw xse;
        }
        
        // These validation errors are just warnings for us as we want to support
        // running from an XML document with XSD validation errors, as long as we can
        // get the metadata we need from the document
        handler.setErrorHandler(new ErrorHandler() {        	
            private String getMessage(SAXParseException e) {
                return "XMLSchema validation problem in: " + getArtifactName( e.getSystemId() ) + ", line: " + e.getLineNumber() + ", column: " + e.getColumnNumber() + "\n" + e.getMessage();
            }
            
            public void error(SAXParseException exception) throws SAXException {            	
            	if (ValidatingXMLStreamReader.this.monitor == null)
            		logger.warning(getMessage(exception));
            	else            		
            		ValidatingXMLStreamReader.this.error("SchemaError", ValidatingXMLStreamReader.this.getClass(), getArtifactName( exception.getSystemId() ), 
                   		exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage());                              
            }
            
            public void fatalError(SAXParseException exception) throws SAXException {            	
            	if (ValidatingXMLStreamReader.this.monitor == null)
            		logger.warning(getMessage(exception));
            	else
            		ValidatingXMLStreamReader.this.error("SchemaFatalError", ValidatingXMLStreamReader.this.getClass(), getArtifactName( exception.getSystemId() ), 
                       	exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage());               
            }
            
            public void warning(SAXParseException exception) throws SAXException {
            	if (ValidatingXMLStreamReader.this.monitor == null)
            		logger.warning(getMessage(exception));
            	else
            		ValidatingXMLStreamReader.this.warning("SchemaWarning", ValidatingXMLStreamReader.this.getClass(), getArtifactName( exception.getSystemId() ), 
                       	exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage());                
            }
            
            private String getArtifactName( String input ) {
            	String artifactName = null;
            	if( ValidatingXMLStreamReader.this.monitor != null ) {
            		artifactName = ValidatingXMLStreamReader.this.monitor.getArtifactName();
            	}
            	if (artifactName == null){
            	    artifactName = input;
            	}
            	return artifactName;
            }
        });
        return handler;
    }
    
    /**
     * Report a warning.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void warning(String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "contribution-validation-messages", Severity.WARNING, model, message, (Object[])messageParameters);
            monitor.problem(problem);
    	}
    }
    
    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem = monitor.createProblem(this.getClass().getName(), "contribution-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    @Override
    public int next() throws XMLStreamException {
        if (getHandler() == null) {
            return super.next();
        }

        int event = super.getEventType();
        try {
            if (event == START_DOCUMENT) {
                // We need to trigger the startDocument()
                handler.startDocument();
            }
            event = super.next();
            validate(event);
        } catch (SAXException e) {
            XMLStreamException xse = new XMLStreamException(e.getMessage(), e);
            error("XMLStreamException", handler, xse);
            throw xse;
        }
        return event;
    }

    private void validate(int event) throws SAXException {
        switch (event) {
            case START_DOCUMENT:
                handler.startDocument();
                break;
            case START_ELEMENT:
                handleStartElement();
                break;
            case PROCESSING_INSTRUCTION:
                handler.processingInstruction(super.getPITarget(), super.getPIData());
                break;
            case CHARACTERS:
            case CDATA:
            case SPACE:
            case ENTITY_REFERENCE:
                handler.characters(super.getTextCharacters(), super.getTextStart(), super.getTextLength());
                break;
            case END_ELEMENT:
                handleEndElement();
                break;
            case END_DOCUMENT:
                handler.endDocument();
                break;
        }
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        if (getHandler() == null) {
            return super.nextTag();
        }
        while (true) {
            int event = super.getEventType();
            try {
                if (event == START_DOCUMENT) {
                    // We need to trigger the startDocument()
                    handler.startDocument();
                }
                event = super.next();
                validate(event);
            } catch (SAXException e) {
                XMLStreamException xse = new XMLStreamException(e);
                error("XMLStreamException", handler, xse);
                throw xse;
            }

            if ((event == CHARACTERS && isWhiteSpace()) // skip whitespace
                || (event == CDATA && isWhiteSpace())
                // skip whitespace
                || event == SPACE
                || event == PROCESSING_INSTRUCTION
                || event == COMMENT) {
                continue;
            }
            if (event != START_ELEMENT && event != END_ELEMENT) {
                throw new XMLStreamException("expected start or end tag", getLocation());
            }
            return event;
        }
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        if (getHandler() == null) {
            return super.getElementText();
        }

        if (getEventType() != START_ELEMENT) {
            return super.getElementText();
        }
        StringBuffer text = new StringBuffer();

        for (;;) {
            int event = next();
            switch (event) {
                case END_ELEMENT:
                    return text.toString();
                    
                case COMMENT:
                case PROCESSING_INSTRUCTION:
                    continue;
                    
                case CHARACTERS:
                case CDATA:
                case SPACE:
                case ENTITY_REFERENCE:
                    text.append(getText());
                    break;
                    
                default:
                    break;
            }
        }
    }
    
    @Override
    public NamespaceContext getNamespaceContext(){
    	return super.getNamespaceContext();
    }
    
    /**
     * Handle a start element event.
     * 
     * @throws SAXException
     */
    private void handleStartElement() throws SAXException {

        // send startPrefixMapping events immediately before startElement event
        int nsCount = super.getNamespaceCount();
        for (int i = 0; i < nsCount; i++) {
            String prefix = super.getNamespacePrefix(i);
            if (prefix == null) { // true for default namespace
                prefix = "";
            }
            handler.startPrefixMapping(prefix, super.getNamespaceURI(i));
        }

        // fire startElement
        QName qname = super.getName();
        String prefix = qname.getPrefix();
        String rawname;
        if (prefix == null || prefix.length() == 0) {
            rawname = qname.getLocalPart();
        } else {
            rawname = prefix + ':' + qname.getLocalPart();
        }
        Attributes attrs = getAttributes();
        handler.startElement(qname.getNamespaceURI(), qname.getLocalPart(), rawname, attrs);
    }

    /**
     * Handle an endElement event.
     * 
     * @throws SAXException
     */
    private void handleEndElement() throws SAXException {

        // fire endElement
        QName qname = super.getName();
        handler.endElement(qname.getNamespaceURI(), qname.getLocalPart(), qname.toString());

        // send endPrefixMapping events immediately after endElement event
        // we send them in the opposite order to that returned but this is not
        // actually required by SAX
        int nsCount = super.getNamespaceCount();
        for (int i = nsCount - 1; i >= 0; i--) {
            String prefix = super.getNamespacePrefix(i);
            if (prefix == null) { // true for default namespace
                prefix = "";
            }
            handler.endPrefixMapping(prefix);
        }
    }

    /**
     * Get the attributes associated with the current START_ELEMENT event.
     * 
     * @return the StAX attributes converted to org.xml.sax.Attributes
     */
    private Attributes getAttributes() {
        AttributesImpl attrs = new AttributesImpl();

        // add namespace declarations
        for (int i = 0; i < super.getNamespaceCount(); i++) {
            String prefix = super.getNamespacePrefix(i);
            String uri = super.getNamespaceURI(i);
            if (prefix == null) {
                attrs.addAttribute("", "", "xmlns", "CDATA", uri);
            } else {
                attrs.addAttribute("", "", "xmlns:" + prefix, "CDATA", uri);
            }
        }

        // Regular attributes
        for (int i = 0; i < super.getAttributeCount(); i++) {
            String uri = super.getAttributeNamespace(i);
            if (uri == null) {
                uri = "";
            }
            String localName = super.getAttributeLocalName(i);
            String prefix = super.getAttributePrefix(i);
            String qname;
            if (prefix == null || prefix.length() == 0) {
                qname = localName;
            } else {
                qname = prefix + ':' + localName;
            }
            String type = super.getAttributeType(i);
            String value = super.getAttributeValue(i);

            attrs.addAttribute(uri, localName, qname, type, value);
        }

        return attrs;
    }

    /**
     * Adapter for mapping Locator information.
     */
    private final class LocatorAdapter implements Locator {

        private LocatorAdapter() {
        }

        public int getColumnNumber() {
            Location location = getLocation();
            return location == null ? 0 : location.getColumnNumber();
        }

        public int getLineNumber() {
            Location location = getLocation();
            return location == null ? 0 : location.getLineNumber();
        }

        public String getPublicId() {
            Location location = getLocation();
            return location == null ? "" : location.getPublicId();
        }

        public String getSystemId() {
            Location location = getLocation();
            return location == null ? "" : location.getSystemId();
        }
    }
    
}
