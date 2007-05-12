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

package org.apache.tuscany.sca.binding.axis2;

import java.util.Map;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.interfacedef.wsdl.introspect.DefaultWSDLInterfaceIntrospector;
import org.apache.tuscany.interfacedef.wsdl.introspect.WSDLInterfaceIntrospector;
import org.apache.tuscany.policy.DefaultPolicyFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.sca.binding.ws.DefaultWebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.xml.WebServiceBindingProcessor;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.invocation.MessageFactoryImpl;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

public class Axis2ModuleActivator implements ModuleActivator {

    public void start(ExtensionPointRegistry registry) {

        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        WebServiceBindingFactory wsFactory = new DefaultWebServiceBindingFactory();
        WSDLFactory wsdlFactory = new DefaultWSDLFactory();
        MessageFactory messageFactory = new MessageFactoryImpl();
        
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        WSDLInterfaceIntrospector introspector = new DefaultWSDLInterfaceIntrospector(wsdlFactory);
        WebServiceBindingProcessor processor =
            new WebServiceBindingProcessor(assemblyFactory, policyFactory, wsFactory, wsdlFactory, introspector); 
        processors.addArtifactProcessor(processor);

        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        ServletHost servletHost = registry.getExtensionPoint(ServletHost.class);
        Axis2BindingProviderFactory providerFactory = new Axis2BindingProviderFactory(servletHost, messageFactory);
        providerFactories.addProviderFactory(providerFactory);
    }

    public void stop(ExtensionPointRegistry registry) {
    }

    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

}
