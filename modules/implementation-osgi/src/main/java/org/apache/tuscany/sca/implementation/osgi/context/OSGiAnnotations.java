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
package org.apache.tuscany.sca.implementation.osgi.context;


import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.context.RequestContextFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.scope.Scope;
import org.apache.tuscany.sca.implementation.java.DefaultJavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.impl.JavaScopeImpl;
import org.apache.tuscany.sca.implementation.java.injection.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.AllowsPassByReferenceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.BaseJavaClassVisitor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ComponentNameProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ConstructorProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ContextProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ConversationProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.DestroyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.EagerInitProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.HeuristicPojoProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.InitProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.PolicyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.PropertyProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ReferenceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ResourceProcessor;
import org.apache.tuscany.sca.implementation.java.introspect.impl.ServiceProcessor;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.osgi.framework.Bundle;


/**
 * OSGi annotation processing
 * OSGi bundles are not introspected by OSGiImplementation when a component is created.
 * Instead if the list of implementation classes is specified in <implementation.osgi/>,
 * the classes are introspected when the bundle is resolved. The classes are loaded using
 * the bundle ClassLoader, and hence the delay in annotation processing is inevitable.
 * There is one other difference compared to implementation.java. While instances (and
 * the instance class) are associated with a component in Java, all Java annotations from
 * the component implementation class apply to all the component instances. In OSGi, 
 * instances are associated with services, and a bundle can register multiple services.
 * Hence annotations from classes need to be stored separately so that the right ones
 * can be associated with the service instance.
 *
 * @version $Rev$ $Date$
 */
public class OSGiAnnotations  {
    
    private Scope scope = Scope.COMPOSITE;
    private boolean isEagerInit;
    
    private String[] classList;
    
    private Bundle[] bundles;    
    
    private RuntimeComponent runtimeComponent;
    private JavaPropertyValueObjectFactory propertyValueFactory;
    private ProxyFactory proxyFactory;
    
    private JavaImplementationFactory javaImplementationFactory;
    private JavaInterfaceFactory javaInterfaceFactory; 
    private PolicyFactory policyFactory;
    private RequestContextFactory requestContextFactory;
    
    private Hashtable<Class<?>, JavaImplementation> javaAnnotationInfo = 
        new Hashtable<Class<?>, JavaImplementation>();
    private Hashtable<JavaImplementation, OSGiPropertyInjector> propertyInjectors = 
        new Hashtable<JavaImplementation, OSGiPropertyInjector>();
    
    private long maxAge = -1;
    private long maxIdleTime = -1;
    
    private boolean annotationsProcessed;
    
    
    
    public OSGiAnnotations(ModelFactoryExtensionPoint modelFactories, 
            String[] classList,
            RuntimeComponent runtimeComponent,
            JavaPropertyValueObjectFactory propertyValueFactory,
            ProxyFactory proxyFactory,
            RequestContextFactory requestContextFactory,
            Bundle mainBundle, 
            ArrayList<Bundle> dependentBundles) {
        
       
        this.classList = classList;
        this.runtimeComponent = runtimeComponent;
        this.propertyValueFactory = propertyValueFactory;
        this.proxyFactory = proxyFactory;
        
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.javaInterfaceFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
        this.javaImplementationFactory = createJavaImplementationFactory(assemblyFactory);
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        
        bundles = new Bundle[dependentBundles.size() + 1];
        bundles[0] = mainBundle;
        for (int i = 0; i < dependentBundles.size(); i++)
            bundles[i + 1] = dependentBundles.get(i);
        
    }
    

    public void processAnnotations() throws IntrospectionException {
        
        if (annotationsProcessed)
            return;
        annotationsProcessed = true;
        for (String className : classList) {
            for (Bundle bundle : bundles) {
                try {
                    Class<?> clazz = bundle.loadClass(className);
 
                    processAnnotations(clazz);
                    
                    break;
                    
                } catch (ClassNotFoundException e) {                    
                }
            }
        }
    }
    
    
    public void injectProperties(Object instance) {
        JavaImplementation javaImpl = getAnnotationInfo(instance);
        if (javaImpl != null) {
            OSGiPropertyInjector injector = propertyInjectors.get(javaImpl);
            if (injector != null)
                injector.injectProperties(instance);
        }
    }


    public Scope getScope() {
        return scope;
    }
    
   

    public boolean isAllowsPassByReference(Object instance, Method method) {
        
        JavaImplementation javaImpl = getAnnotationInfo(instance);
        if (javaImpl == null) {
            return false;
        }
        if (javaImpl.isAllowsPassByReference()) {
            return true;
        }
        return javaImpl.isAllowsPassByReference(method);
    }
    
    
    public boolean isEagerInit() {
        return isEagerInit;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }
    
    public Method getInitMethod(Object instance) {
        JavaImplementation javaImpl = getAnnotationInfo(instance);
        return javaImpl == null? null : javaImpl.getInitMethod();
    }
    

    public Method getDestroyMethod(Object instance) {
        JavaImplementation javaImpl = getAnnotationInfo(instance);
        return javaImpl == null? null : javaImpl.getDestroyMethod();       
    }
    

    /*
     * Get the annotation corresponding to an instance
     * 
     */
    private JavaImplementation getAnnotationInfo(final Object instance) {
    	
    	// The simplest case where the implementation class was listed under the
    	// classes attribute of <implementation.osgi/>, or this is the second call
    	// to this method for the implementation class.
        // Allow privileged access to get classloader. Requires getClassLoader in security policy.
        JavaImplementation javaImpl = AccessController.doPrivileged(new PrivilegedAction<JavaImplementation>() {
            public JavaImplementation run() {
                return javaAnnotationInfo.get(instance.getClass());
            }
        });
        if (javaImpl != null)
            return javaImpl;
    	
        // Process annotations from the instance class.
        try {
            return processAnnotations(instance.getClass());
        } catch (IntrospectionException e) {
            // e.printStackTrace();
        }
        
        return null;
    }
    

    private JavaImplementation processAnnotations(Class<?> clazz)
            throws IntrospectionException {

        JavaImplementation javaImpl = javaImplementationFactory.createJavaImplementation(clazz);

        javaAnnotationInfo.put(clazz, javaImpl);

        OSGiPropertyInjector propertyInjector = new OSGiPropertyInjector(
                javaImpl, runtimeComponent, propertyValueFactory, proxyFactory, requestContextFactory);

        propertyInjectors.put(javaImpl, propertyInjector);

        if (javaImpl.isEagerInit())
            isEagerInit = true;
        if (javaImpl.getMaxAge() != -1)
            maxAge = javaImpl.getMaxAge();
        if (javaImpl.getMaxIdleTime() != -1)
            maxIdleTime = javaImpl.getMaxIdleTime();
        if (javaImpl.getJavaScope() != JavaScopeImpl.COMPOSITE)
            scope = new Scope(javaImpl.getJavaScope().getScope());
        
        return javaImpl;
    }
        
    
    
    
    private JavaImplementationFactory  createJavaImplementationFactory(AssemblyFactory assemblyFactory) {
        JavaImplementationFactory javaImplementationFactory = new DefaultJavaImplementationFactory();
        
        // Create the list of class visitors
        BaseJavaClassVisitor[] extensions =
            new BaseJavaClassVisitor[] {
                                        new ConstructorProcessor(assemblyFactory),
                                        new AllowsPassByReferenceProcessor(assemblyFactory),
                                        new ComponentNameProcessor(assemblyFactory),
                                        new ContextProcessor(assemblyFactory),
                                        new ConversationProcessor(assemblyFactory),
                                        new DestroyProcessor(assemblyFactory),
                                        new EagerInitProcessor(assemblyFactory),
                                        new InitProcessor(assemblyFactory),
                                        new PropertyProcessor(assemblyFactory),
                                        new ReferenceProcessor(assemblyFactory, javaInterfaceFactory),
                                        new ResourceProcessor(assemblyFactory),
                                        new OSGiScopeProcessor(assemblyFactory),
                                        new ServiceProcessor(assemblyFactory, javaInterfaceFactory),
                                        new HeuristicPojoProcessor(assemblyFactory, javaInterfaceFactory),
                                        new PolicyProcessor(assemblyFactory, policyFactory)};
        for (JavaClassVisitor extension : extensions) {
            javaImplementationFactory.addClassVisitor(extension);
        }
        
        return javaImplementationFactory;
    }
    
    private class OSGiScopeProcessor extends BaseJavaClassVisitor {
        
        public OSGiScopeProcessor(AssemblyFactory factory) {
            super(factory);
        }

        @Override
        public <T> void visitClass(Class<T> clazz,
                                   JavaImplementation type)
            throws IntrospectionException {
            org.osoa.sca.annotations.Scope annotation = clazz.getAnnotation(org.osoa.sca.annotations.Scope.class);
            if (annotation == null) {
                type.setJavaScope(JavaScopeImpl.COMPOSITE);
                return;
            }
            String name = annotation.value();
            JavaScopeImpl scope;
            if ("COMPOSITE".equals(name)) {
                scope = JavaScopeImpl.COMPOSITE;
            } else if ("SESSION".equals(name)) {
                scope = JavaScopeImpl.SESSION;
            } else if ("CONVERSATION".equals(name)) {
                scope = JavaScopeImpl.CONVERSATION;
            } else if ("REQUEST".equals(name)) {
                scope = JavaScopeImpl.REQUEST;
            } else {
                scope = new JavaScopeImpl(name);
            }
            type.setJavaScope(scope);
        }
    }

    
}
