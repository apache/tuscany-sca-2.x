/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.celtix.loader;

import java.io.IOException;
import java.net.URL;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;

import org.w3c.dom.Element;

import org.apache.tuscany.core.loader.WSDLDefinitionRegistry;

import org.objectweb.celtix.BusException;
import org.objectweb.celtix.wsdl.WSDLManager;

public class TuscanyWSDLManager implements WSDLManager {
    WSDLDefinitionRegistry wsdlDefinitionRegistry;

    public TuscanyWSDLManager(WSDLDefinitionRegistry w) throws BusException {
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
            //FIXME pass the current ResourceLoader 
            return wsdlDefinitionRegistry.loadDefinition(null, url, null);
        } catch (IOException e) {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, e.getMessage());
        }
    }

    public Definition getDefinition(String url) throws WSDLException {
        try {
            //The namespace is the wsdl targetNamesapce, it is only used
            //when the wsdl is created into cache. we are ok here to set it to null.
            //FIXME pass the current ResourceLoader
            return wsdlDefinitionRegistry.loadDefinition(null, new URL(url), null);
        } catch (IOException e) {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, e.getMessage());
        }
    }

    public Definition getDefinition(Element el) throws WSDLException {
        //Not supported
        return null;
    }

    public Definition getDefinition(Class<?> sei) throws WSDLException {
        //Not supported
        return null;
    }

    public void addDefinition(Object key, Definition wsdl) {
        //Not supported
    }

    public void shutdown() {
    }


}
