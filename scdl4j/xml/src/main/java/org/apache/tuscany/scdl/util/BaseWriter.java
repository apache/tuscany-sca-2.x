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

package org.apache.tuscany.scdl.util;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.tuscany.assembly.model.AbstractProperty;
import org.apache.tuscany.assembly.model.ComponentType;
import org.apache.tuscany.assembly.model.ConstrainingType;
import org.apache.tuscany.assembly.model.Property;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * A test writer to test the usability of the assembly model API when writing SCDL
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseWriter extends XMLFilterImpl {

    protected final static String sca10 = "http://www.osoa.org/xmlns/sca/1.0";
    protected ContentHandler out;

    /**
     * This is where you write the logic to produce SCDL.
     * @throws SAXException
     */
    abstract protected void write() throws SAXException;
    
    public void parse(InputSource input) throws SAXException, IOException {
    	out.startDocument();
    	write();
    	out.endDocument();
    }
    
    public void setContentHandler(ContentHandler handler) {
    	super.setContentHandler(handler);
    	out = handler;
    }
    
    protected void start(String uri, String name, Attr... attrs) throws SAXException {
    	out.startElement(uri, null, name, attributes(attrs));
    }

    protected void start(String name, Attr... attrs) throws SAXException {
    	out.startElement(sca10, null, name, attributes(attrs));
    }
    
    protected void end(String uri, String name) throws SAXException {
    	out.endElement(uri, null, name);
    }

    protected void end(String name) throws SAXException {
    	out.endElement(sca10, null, name);
    }

    protected Attributes attributes(Attr... attrs) {
    	AttributesImpl attributes = new AttributesImpl();
    	for (Attr attr: attrs) {
    		if (attr != null)
    			attr.write(attributes);
    	}
    	return attributes;
    }

    protected QName getConstrainingType(ComponentType componentType) {
    	ConstrainingType constrainingType = componentType.getConstrainingType();
    	if (constrainingType!=null)
    		return constrainingType.getName();
    	else
    		return null;
    }

    protected Attributes abstractPropertyAttributes(AbstractProperty prop) {
    	Attributes attributes = attributes(
	        new Attr("name", prop.getName()),
	        new Attr("many", prop.isMany()),
	        new Attr("mustSupply", prop.isMustSupply()),
	        new Attr("element", prop.getXSDElement()),
	        new Attr("type", prop.getXSDType())
    	);
        // TODO handle default value
    	return attributes;
    }

    protected Attributes propertyAttributes(Property prop) {
        // TODO handle property value
        return abstractPropertyAttributes(prop);
    }
}
