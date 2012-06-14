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
package org.apache.tuscany.sca.runtime;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

/**
 * Shows how to create and run composites dynamically
 */
public class DynamicTestCase {

    @Test
    public void testInstalledContribution() throws NoSuchServiceException, NoSuchDomainException, ContributionReadException, ActivationException, ValidationException, MalformedURLException, ClassNotFoundException, IntrospectionException, IllegalArgumentException, InvocationTargetException,
        IllegalAccessException {

        // get the various factories that will be needed
        TuscanyRuntime tuscanyRuntime = TuscanyRuntime.newInstance();
        ExtensionPointRegistry extensionPoints = tuscanyRuntime.getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);

        // Create a contribution
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI("testContribution");
        ModelResolverExtensionPoint modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
        contribution.setModelResolver(modelResolver);
        contribution.setClassLoader(new URLClassLoader(new URL[] {new File("src/test/resources/sample-helloworld.jar").toURI().toURL()}));

        // Create a composite
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        Composite composite = assemblyFactory.createComposite();
        composite.setURI("testComposite");
        composite.setName(new QName("testComposite"));

        // create a component
        Component component = assemblyFactory.createComponent();
        component.setName("testComponent");
        JavaImplementationFactory javaImplementationFactory = modelFactories.getFactory(JavaImplementationFactory.class);
        JavaImplementation javaImplementation = javaImplementationFactory.createJavaImplementation(contribution.getClassLoader().loadClass("sample.HelloworldImpl"));
        javaImplementation.setJavaClass(contribution.getClassLoader().loadClass("sample.HelloworldImpl"));
        component.setImplementation(javaImplementation);

        // add the component to the composite
        composite.getComponents().add(component);

        // add the composite to the contribution
        contribution.addComposite(composite);

        // Now run the composite with a Tuscany Node
        Node node = tuscanyRuntime.createNode();
        node.installContribution(contribution, null);
        node.startComposite(contribution.getURI(), composite.getURI());

        // test that the service has started and can be invoked
        testService(node, contribution.getClassLoader());

        node.stop();
        tuscanyRuntime.stop();
    }

    private void testService(Node node, ClassLoader classLoader) throws ClassNotFoundException, NoSuchServiceException, NoSuchDomainException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {
        Class<?> interfaze = classLoader.loadClass("sample.Helloworld");
        Object clientProxy = node.getService(interfaze, "testComponent/Helloworld");
        Method m = interfaze.getMethods()[0]; // the helloworld interface just has a single method "sayHello"
        Object response = m.invoke(clientProxy, new Object[] {"Ariana"});
        Assert.assertEquals("Hello Ariana", response);
    }

}
