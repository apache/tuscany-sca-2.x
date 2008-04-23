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

package org.apache.tuscany.sca.implementation.java.impl;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
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
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * A module activator for the Java implementation model.
 *
 * @version $Rev$ $Date$
 */
public class JavaImplementationActivator implements ModuleActivator {

    public void start(ExtensionPointRegistry registry) {
        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        JavaInterfaceFactory javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
        PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);
        
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

        JavaImplementationFactory javaImplementationFactory = modelFactories.getFactory(JavaImplementationFactory.class);
        for (JavaClassVisitor extension : extensions) {
            javaImplementationFactory.addClassVisitor(extension);
        }

    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
