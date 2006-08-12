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
package org.apache.tuscany.binding.celtix;

import java.io.IOException;
import java.net.URL;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;

import org.w3c.dom.Element;
import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistry;

import org.objectweb.celtix.wsdl.WSDLManager;

/**
 *
 * @version $Rev$ $Date$
 */
public class TuscanyWSDLManager implements WSDLManager {
    WSDLDefinitionRegistry wsdlDefinitionRegistry;

    public TuscanyWSDLManager(WSDLDefinitionRegistry w) {
        wsdlDefinitionRegistry = w;
    }

    public WSDLFactory getWSDLFactory() {
        //Not supported
        return null;
    }

    public ExtensionRegistry getExtenstionRegistry() {
        return wsdlDefinitionRegistry.getExtensionRegistry();
    }

    public Definition getDefinition(URL url) throws WSDLException {
        try {
            return wsdlDefinitionRegistry.loadDefinition(null, url);
        } catch (IOException e) {
            //FIXME
            throw new WSDLException("", "", e);
        }
    }

    public Definition getDefinition(String url) throws WSDLException {
        try {
            //The namespace is the wsdl targetNamesapce, it is only used
            //when the wsdl is created into cache. we are ok here to set it to null.
            //FIXME pass the current ResourceLoader
            return wsdlDefinitionRegistry.loadDefinition(null, new URL(url));
        } catch (IOException e) {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, e.getMessage());
        }
    }

    public Definition getDefinition(Element el) throws WSDLException {
        throw new UnsupportedOperationException();
    }

    public Definition getDefinition(Class<?> sei) throws WSDLException {
        throw new UnsupportedOperationException();
    }

    public void addDefinition(Object key, Definition wsdl) {
        throw new UnsupportedOperationException();
    }

    public void shutdown() {
    }


}
