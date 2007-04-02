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
package org.apache.tuscany.idl.java.xml;

import org.apache.tuscany.idl.java.JavaFactory;
import org.apache.tuscany.idl.java.JavaInterface;
import org.apache.tuscany.sca.idl.Interface;
import org.apache.tuscany.scdl.Constants;
import org.apache.tuscany.scdl.InterfaceHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A content handler for Java interfaces and implementations.
 * 
 * @version $Rev$ $Date$
 */
public class JavaInterfaceHandler extends DefaultHandler implements InterfaceHandler {

    private JavaFactory javaFactory;
    private JavaInterface javaInterface;

    public JavaInterfaceHandler(JavaFactory javaFactory) {
        this.javaFactory = javaFactory;
    }

    public void startDocument() throws SAXException {
        javaInterface = null;
    }

    public void startElement(String uri, String name, String qname, Attributes attr) throws SAXException {

        if (Constants.SCA10_NS.equals(uri)) {

            if (JavaConstants.INTERFACE_JAVA.equals(name)) {

                // Parse a Java interface
                javaInterface = javaFactory.createJavaInterface();
                javaInterface.setUnresolved(true);
                javaInterface.setName(attr.getValue(JavaConstants.INTERFACE));
            }
        }
    }

    public Interface getInterface() {
        return javaInterface;
    }

}
