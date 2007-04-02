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

import org.apache.tuscany.assembly.model.Implementation;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.scdl.Constants;
import org.apache.tuscany.scdl.ImplementationHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A content handler for Java implementations.
 * 
 * @version $Rev$ $Date$
 */
public class JavaImplementationHandler extends DefaultHandler implements ImplementationHandler {

    private JavaImplementationFactory javaFactory;
    private JavaImplementation javaImplementation;

    public JavaImplementationHandler(JavaImplementationFactory javaFactory) {
        this.javaFactory = javaFactory;
    }

    public void startDocument() throws SAXException {
        javaImplementation = null;
    }

    public void startElement(String uri, String name, String qname, Attributes attr) throws SAXException {

        if (Constants.SCA10_NS.equals(uri)) {

            if (JavaImplementationConstants.IMPLEMENTATION_JAVA.equals(name)) {

                // Parse a Java implementation
                javaImplementation = javaFactory.createJavaImplementation();
                javaImplementation.setUnresolved(true);
                javaImplementation.setName(attr.getValue(JavaImplementationConstants.CLASS));
            }
        }
    }

    public Implementation getImplementation() {
        return javaImplementation;
    }

}
