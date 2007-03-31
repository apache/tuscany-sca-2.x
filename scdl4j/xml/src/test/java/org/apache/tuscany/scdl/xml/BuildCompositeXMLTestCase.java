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

package org.apache.tuscany.scdl.xml;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tuscany.scdl.xml.util.Attr;
import org.apache.tuscany.scdl.xml.util.Element;
import org.apache.tuscany.scdl.xml.util.ElementWriter;

import junit.framework.TestCase;

public class BuildCompositeXMLTestCase extends TestCase {
	
	public void testBuildComposite() throws Exception {
		
		Element element = new Element("http://www.osoa.org/xmlns/sca/1.0", "composite",
				
			new Element("service",
				new Attr("name", "AccountService"),
				new Attr("promote", "AccountServiceComponent/AccountService")),
				
			new Element("component",
				new Attr("name", "AccountServiceComponent"),
				new Element("service",
					new Attr("name", "AccountService"),
					new Element("interface.java",
						new Attr("interface", "bigbank.account.AccountService")
					)
				),
				new Element("reference",
					new Attr("name", "stockQuoteService"),
					new Element("interface.java",
						new Attr("interface", "bigbank.stockquote.StockQuoteService")
					)
				),
				new Element("implementation.java",
					new Attr("class", "bigbank.account.AccountServiceImpl")
				)
			)
		);
		
		ElementWriter writer = new ElementWriter(element);
		
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("indent", "yes");
        System.out.println();
        transformer.transform(new SAXSource(writer, null), new StreamResult(System.out));
        System.out.println();
		
	}

}
