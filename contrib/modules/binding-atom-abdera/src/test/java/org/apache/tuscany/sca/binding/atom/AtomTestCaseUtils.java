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
package org.apache.tuscany.sca.binding.atom;

import java.io.IOException;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;

/**
 * Utilities to help print and test various aspects of entity tag support.
 */
public class AtomTestCaseUtils {

    public static void prettyPrint(Abdera abdera, Base doc) throws IOException {
        WriterFactory factory = abdera.getWriterFactory();
        Writer writer = factory.getWriter("prettyxml");
        writer.writeTo(doc, System.out);
        System.out.println();
 	}

    public static void printRequestHeaders( String title, String indent, RequestOptions request ) {
     	System.out.println( title );
     	if ( request == null ) {
     		System.out.println( indent + " request is null");
     		return;
     	}
     	String [] headerNames = request.getHeaderNames();
     	for ( String headerName: headerNames) {
     		String header = request.getHeader(headerName);
    	       System.out.println( indent + " header name,value=" + headerName + "," + header );	
     	}    	               	           
     }
    
     public static void printResponseHeaders( String title, String indent, ClientResponse response ) {
      	System.out.println( title );
     	if ( response == null ) {
     		System.out.println( indent + " response is null");
     		return;
     	}
     	String [] headerNames = response.getHeaderNames();
     	for ( String headerName: headerNames) {
     	    String header = response.getHeader(headerName);
     	    System.out.println( indent + " header name,value=" + headerName + "," + header );
     	}
     	               	           
     }

	public static Entry newEntry(String value) {
		Abdera abdera = new Abdera();
		Entry entry = abdera.newEntry();
		entry.setTitle("customer " + value);

		Content content = abdera.getFactory().newContent();
		content.setContentType(Content.Type.TEXT);
		content.setValue(value);
		entry.setContentElement(content);

		return entry;
	}

	public static Entry updateEntry(Entry entry, String value) {
		Abdera abdera = new Abdera();
		entry.setTitle("customer " + value);

		Content content = abdera.getFactory().newContent();
		content.setContentType(Content.Type.TEXT);
		content.setValue(value);
		entry.setContentElement(content);

		return entry;
	}		
	
}
