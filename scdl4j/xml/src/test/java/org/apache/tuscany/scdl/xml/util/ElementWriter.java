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

package org.apache.tuscany.scdl.xml.util;

import java.io.IOException;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * A test writer to test the usability of the assembly model API when writing SCDL
 * 
 * @version $Rev$ $Date$
 */
public class ElementWriter extends XMLFilterImpl {

	private Element element;
    private ContentHandler out;

    public ElementWriter(Element element) {
    	this.element = element;
	}
    
    public void parse(InputSource input) throws SAXException, IOException {
    	out.startDocument();
    	element.write(out);
    	out.endDocument();
    }
    
    public void setContentHandler(ContentHandler handler) {
    	super.setContentHandler(handler);
    	out = handler;
    }
    
}
