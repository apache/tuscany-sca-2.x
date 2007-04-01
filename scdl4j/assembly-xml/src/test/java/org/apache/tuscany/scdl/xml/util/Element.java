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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Element extends Base {

	private String uri;
	private String name;
	private List<Base> children = new ArrayList<Base>();
	
	public Element(String uri, String name, Base... children) {
		this.uri = uri;
		this.name = name;
		this.children.addAll(Arrays.asList(children));
	}

	public Element(String name, Base... children) {
		this.name =name;
		this.children.addAll(Arrays.asList(children));
	}
	
	public void add(Base... children) {
		this.children.addAll(Arrays.asList(children));
	}
	
	void write(AttributesImpl attrs) {
	}
	
	public void write(ContentHandler out) throws SAXException {
		AttributesImpl attrs = new AttributesImpl();
		for (Base child: children) {
			if (child != null)
				child.write(attrs);
		}
		out.startElement(uri, null, name, attrs);
		for (Base child: children) {
			if (child != null)
				child.write(out);
		}
		out.endElement(uri, null, name);
	}

}
