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

package org.apache.tuscany.sca.interfacedef.wsdl.module;

import javax.wsdl.WSDLException;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.XSDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.WSDLModelResolver;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.XSDModelResolver;

/**
 * @version $Rev$ $Date$
 */
public class WSDLInterfaceRuntimeModuleActivator implements ModuleActivator {
    
    public Object[] getExtensionPoints() {
        return null;
    }

    public void start(ExtensionPointRegistry registry) {
        
        // Register the WSDL interface factory
        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class); 
        WSDLFactory wsdlFactory = new DefaultWSDLFactory();
        modelFactories.addFactory(wsdlFactory);
        javax.wsdl.factory.WSDLFactory wsdl4jFactory;
        try {
            wsdl4jFactory = javax.wsdl.factory.WSDLFactory.newInstance();
        } catch (WSDLException e) {
            throw new IllegalStateException(e);
        }
        modelFactories.addFactory(wsdl4jFactory);
        
        ModelResolverExtensionPoint resolvers = registry.getExtensionPoint(ModelResolverExtensionPoint.class);
        resolvers.addResolver(WSDLDefinition.class, WSDLModelResolver.class);
        resolvers.addResolver(XSDefinition.class, XSDModelResolver.class);
    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
