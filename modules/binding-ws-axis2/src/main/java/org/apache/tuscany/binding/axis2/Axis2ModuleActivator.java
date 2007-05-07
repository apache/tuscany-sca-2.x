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

package org.apache.tuscany.binding.axis2;

import java.util.Map;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.binding.ws.DefaultWebServiceBindingFactory;
import org.apache.tuscany.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.binding.ws.xml.WebServiceBindingProcessor;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.http.ServletHost;
import org.apache.tuscany.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.interfacedef.wsdl.introspect.DefaultWSDLInterfaceIntrospector;
import org.apache.tuscany.interfacedef.wsdl.introspect.WSDLInterfaceIntrospector;
import org.apache.tuscany.policy.DefaultPolicyFactory;
import org.apache.tuscany.policy.PolicyFactory;

public class Axis2ModuleActivator implements ModuleActivator {

    private Axis2BindingBuilder builder;

    public void start(ExtensionPointRegistry registry) {

        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        WebServiceBindingFactory wsFactory = new DefaultWebServiceBindingFactory();
        WSDLFactory wsdlFactory = new DefaultWSDLFactory();
        WSDLInterfaceIntrospector introspector = new DefaultWSDLInterfaceIntrospector(wsdlFactory);
        processors.addArtifactProcessor(new WebServiceBindingProcessor(
                                                                              assemblyFactory, policyFactory, wsFactory,
                                                                              wsdlFactory, introspector));

        ServletHost servletHost = registry.getExtensionPoint(ServletHost.class);
        
        BuilderRegistry builderRegistry = registry.getExtensionPoint(BuilderRegistry.class);
        builder = new Axis2BindingBuilder();
        builder.setBuilderRegistry(builderRegistry);
        builder.setServletHost(servletHost);
        builder.init();

    }

    public void stop(ExtensionPointRegistry registry) {
        //FIXME
        // release resources held by bindings
        // needed because the stop methods in ReferenceImpl and ServiceImpl aren't being called
        // TODO: revisit this as part of the lifecycle work
        builder.destroy(); // release connections
    }

    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

}
