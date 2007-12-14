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

package org.apache.tuscany.sca.implementation.java.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.context.ComponentContextFactory;
import org.apache.tuscany.sca.context.ContextFactoryExtensionPoint;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.util.ServiceDeclaration;
import org.apache.tuscany.sca.contribution.util.ServiceDiscovery;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.invocation.CglibProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.AllowsPassByReferenceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.BaseJavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ComponentNameProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ConstructorProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ContextProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ConversationIDProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ConversationProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.DestroyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.EagerInitProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.HeuristicPojoProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.InitProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.PolicyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.PropertyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ReferenceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ResourceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ScopeProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ServiceProcessor;
import org.apache.tuscany.sca.implementation.java.invocation.JavaCallbackRuntimeWireProcessor;
import org.apache.tuscany.sca.implementation.java.invocation.JavaImplementationProviderFactory;
import org.apache.tuscany.sca.implementation.java.invocation.JavaPolicyHandlingRuntimeWireProcessor;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.util.PolicyHandler;
import org.apache.tuscany.sca.policy.util.PolicyHandlerTuple;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;

/**
 * @version $Rev$ $Date$
 */
public class JavaRuntimeModuleActivator implements ModuleActivator {

    public JavaRuntimeModuleActivator() {
    }

    public void start(ExtensionPointRegistry registry) {

        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
        PolicyFactory policyFactory = factories.getFactory(PolicyFactory.class);

        MessageFactory messageFactory = factories.getFactory(MessageFactory.class);
        ProxyFactoryExtensionPoint proxyFactory = registry.getExtensionPoint(ProxyFactoryExtensionPoint.class);
        proxyFactory.setClassProxyFactory(new CglibProxyFactory(messageFactory, proxyFactory
            .getInterfaceContractMapper()));

        JavaInterfaceFactory javaFactory = factories.getFactory(JavaInterfaceFactory.class);
        JavaImplementationFactory javaImplementationFactory = factories.getFactory(JavaImplementationFactory.class);

        BaseJavaClassVisitor[] extensions =
            new BaseJavaClassVisitor[] {new ConstructorProcessor(assemblyFactory),
                                        new AllowsPassByReferenceProcessor(assemblyFactory),
                                        new ComponentNameProcessor(assemblyFactory),
                                        new ContextProcessor(assemblyFactory),
                                        new ConversationIDProcessor(assemblyFactory),
                                        new ConversationProcessor(assemblyFactory),
                                        new DestroyProcessor(assemblyFactory), new EagerInitProcessor(assemblyFactory),
                                        new InitProcessor(assemblyFactory), new PropertyProcessor(assemblyFactory),
                                        new ReferenceProcessor(assemblyFactory, javaFactory),
                                        new ResourceProcessor(assemblyFactory), new ScopeProcessor(assemblyFactory),
                                        new ServiceProcessor(assemblyFactory, javaFactory),
                                        new HeuristicPojoProcessor(assemblyFactory, javaFactory),
                                        new PolicyProcessor(assemblyFactory, policyFactory)};
        for (JavaClassVisitor extension : extensions) {
            javaImplementationFactory.addClassVisitor(extension);
        }

        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        MediatorImpl mediator = new MediatorImpl(dataBindings, transformers);
        JavaPropertyValueObjectFactory factory = new JavaPropertyValueObjectFactory(mediator);

        ContextFactoryExtensionPoint contextFactories = registry.getExtensionPoint(ContextFactoryExtensionPoint.class);
        ComponentContextFactory componentContextFactory = contextFactories.getFactory(ComponentContextFactory.class);
        RequestContextFactory requestContextFactory = contextFactories.getFactory(RequestContextFactory.class);

        Map<ClassLoader, List<PolicyHandlerTuple>> policyHandlerClassNames = null;
        try {
            policyHandlerClassNames = loadPolicyHandlerClassnames();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JavaImplementationProviderFactory javaImplementationProviderFactory =
            new JavaImplementationProviderFactory(proxyFactory, dataBindings, factory, componentContextFactory,
                                                  requestContextFactory, policyHandlerClassNames);

        ProviderFactoryExtensionPoint providerFactories =
            registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(javaImplementationProviderFactory);

        InterfaceContractMapper interfaceContractMapper = registry.getExtensionPoint(InterfaceContractMapper.class);
        RuntimeWireProcessorExtensionPoint wireProcessorExtensionPoint =
            registry.getExtensionPoint(RuntimeWireProcessorExtensionPoint.class);
        if (wireProcessorExtensionPoint != null) {
            wireProcessorExtensionPoint.addWireProcessor(new JavaCallbackRuntimeWireProcessor(interfaceContractMapper,
                                                                                              javaFactory));
            wireProcessorExtensionPoint.addWireProcessor(new JavaPolicyHandlingRuntimeWireProcessor());
        }
    }

    public void stop(ExtensionPointRegistry registry) {
    }

    private static QName getQName(String qname) {
        if (qname == null) {
            return null;
        }
        qname = qname.trim();
        if (qname.startsWith("{")) {
            int h = qname.indexOf('}');
            if (h != -1) {
                return new QName(qname.substring(1, h), qname.substring(h + 1));
            }
        } else {
            int h = qname.indexOf('#');
            if (h != -1) {
                return new QName(qname.substring(0, h), qname.substring(h + 1));
            }
        }
        throw new IllegalArgumentException("Invalid qname: " + qname);
    }

    private Map<ClassLoader, List<PolicyHandlerTuple>> loadPolicyHandlerClassnames() throws IOException {
        // Get the processor service declarations
        Set<ServiceDeclaration> sds;
        try {
            sds = ServiceDiscovery.getInstance().getServiceDeclarations(PolicyHandler.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        Map<ClassLoader, List<PolicyHandlerTuple>> handlerTuples = new Hashtable<ClassLoader, List<PolicyHandlerTuple>>();
        for (ServiceDeclaration sd : sds) {
            ClassLoader cl = sd.getClassLoader();
            
            List<PolicyHandlerTuple> handlerTupleList = handlerTuples.get(cl);
            if ( handlerTupleList == null ) {
                handlerTupleList = new ArrayList<PolicyHandlerTuple>();
                handlerTuples.put(cl, handlerTupleList);
            }
            Map<String, String> attributes = sd.getAttributes();
            String intentName = attributes.get("intent");
            QName intentQName = getQName(intentName);
            String policyModelClassName = attributes.get("model");
            handlerTupleList.add(new PolicyHandlerTuple(sd.getClassName(), intentQName, policyModelClassName));
        }

        /*Map<ClassLoader, Map<QName, String>> policyHandlerClassNames = new HashMap<ClassLoader, Map<QName, String>>();

        for (ServiceDeclaration sd : sds) {
            Map<String, String> attributes = sd.getAttributes();
            String qname = attributes.get("qname");
            QName name = getQName(qname);
            ClassLoader cl = sd.getClassLoader();
            Map<QName, String> map = policyHandlerClassNames.get(cl);
            if (map == null) {
                map = new HashMap<QName, String>();
                policyHandlerClassNames.put(cl, map);
            }
            map.put(name, sd.getClassName());
        }
        */
        return handlerTuples;
    }

}
