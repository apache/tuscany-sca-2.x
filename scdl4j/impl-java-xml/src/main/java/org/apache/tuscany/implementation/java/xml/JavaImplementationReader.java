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

package org.apache.tuscany.implementation.java.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.scdl.stax.Loader;

public class JavaImplementationReader implements Loader<JavaImplementation>, JavaImplementationConstants {

    private JavaImplementationFactory javaFactory;
    
    public JavaImplementationReader(JavaImplementationFactory javaFactory) {
            this.javaFactory = javaFactory;
    }
    
    public JavaImplementation load(XMLStreamReader reader) throws XMLStreamException {

        // Read an <interface.java>
        JavaImplementation javaImplementation = javaFactory.createJavaImplementation();
        javaImplementation.setUnresolved(true);
        javaImplementation.setName(reader.getAttributeValue(null, CLASS));
        
        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_JAVA_QNAME.equals(reader.getName()))
                break;
        }
        return javaImplementation;
    }
}
