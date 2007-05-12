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

package org.apache.tuscany.interfacedef.wsdl.module;

import java.util.Map;

import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.interfacedef.wsdl.introspect.DefaultWSDLInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.wsdl.introspect.WSDLInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.WSDLDocumentProcessor;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.WSDLInterfaceProcessor;

/**
 * @version $Rev$ $Date$
 */
public class WSDLInterfaceRuntimeModuleActivator implements ModuleActivator {
    
    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

    public void start(ExtensionPointRegistry registry) {
        
        WSDLFactory wsdlFactory = new DefaultWSDLFactory();
        WSDLInterfaceIntrospector interfaceIntrospector = new DefaultWSDLInterfaceIntrospector(wsdlFactory);
        
        // Register <interface.wsdl> processor
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.addArtifactProcessor(new WSDLInterfaceProcessor(wsdlFactory, interfaceIntrospector));
        
        // Register .wsdl document processor 
        URLArtifactProcessorExtensionPoint documentProcessors = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        documentProcessors.addArtifactProcessor(new WSDLDocumentProcessor(wsdlFactory, null));
    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
