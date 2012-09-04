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

import java.io.IOException;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.impl.NodeImpl;
import org.apache.tuscany.sca.implementation.java.IntrospectionException;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oasisopen.sca.ServiceRuntimeException;
import org.xml.sax.SAXException;

public class DOMInvokerFaultsTestCase {

    static TuscanyRuntime tuscanyRuntime;
    static Node node;
    static DOMHelper domHelper;

    @Test
    public void testOK() throws Throwable {
        DOMInvoker domInvoker = node.getDOMInvoker("testComponent");
        org.w3c.dom.Node arg = getRequestDOM("petra");
        org.w3c.dom.Node response = domInvoker.invoke("sayHello", arg);
        Assert.assertEquals("Hello petra", getResponseString(response));
    }
    @Test
    public void testCheckedException() throws Throwable {
        DOMInvoker domInvoker = node.getDOMInvoker("testComponent");
        org.w3c.dom.Node arg = getRequestDOM("beate");
        try {
           org.w3c.dom.Node response = domInvoker.invoke("sayHello", arg);
        } catch (ServiceRuntimeException e) {
            if (e.getCause() instanceof FaultException) {
                FaultException fe = (FaultException) e.getCause();
                String xml = domHelper.saveAsString((org.w3c.dom.Node)fe.getFaultInfo());
                Assert.assertTrue(xml.contains("<message>Bad Beate</message>"));
                return;
            }
        }
        Assert.fail();
    }
    @Test
    public void testRuntimeException() throws Throwable {
        DOMInvoker domInvoker = node.getDOMInvoker("testComponent");
        org.w3c.dom.Node arg = getRequestDOM("bang");
        try {
            org.w3c.dom.Node response = domInvoker.invoke("sayHello", arg);
         } catch (ServiceRuntimeException e) {
             if (e.getCause() instanceof RuntimeException) {
                 RuntimeException re = (RuntimeException) e.getCause();
                 Assert.assertEquals("got bang", re.getMessage());
                 return;
             }
         }
         Assert.fail();
    }

    private String getResponseString(org.w3c.dom.Node responseDOM) {
        String xml = domHelper.saveAsString(responseDOM); 
        int x = xml.indexOf("<return>") + "<return>".length();
        int y = xml.indexOf("</return>");
        return xml.substring(x, y);
    }

    private org.w3c.dom.Node getRequestDOM(String name) {
        try {

            String xml = "<ns2:sayHello xmlns:ns2=\"http://sample/\"><arg0>"+ name + "</arg0></ns2:sayHello>";
            return domHelper.load(xml);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        createComponent();
        ExtensionPointRegistry registry = ((NodeImpl)node).getExtensionPointRegistry();
        domHelper = DOMHelper.getInstance(registry);

    }
    
    @AfterClass
    public static void stop() throws Exception {
        node.stop();
        tuscanyRuntime.stop();
    }
    
    public static void createComponent() throws ClassNotFoundException, IntrospectionException, ContributionReadException, ActivationException, ValidationException {
        tuscanyRuntime = TuscanyRuntime.newInstance();
        node = TuscanyRuntime.newInstance().createNode();
        
        // get the various factories that will be needed
        ExtensionPointRegistry extensionPoints = tuscanyRuntime.getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);

        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        utilities.getUtility(RuntimeProperties.class).getProperties().setProperty(RuntimeProperties.QUIET_LOGGING, "true");
        
        // Create a contribution
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI("testContribution");
        ModelResolverExtensionPoint modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
        contribution.setModelResolver(modelResolver);
        contribution.setClassLoader(DOMInvokerFaultsTestCase.class.getClassLoader());

        // Create a composite
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        Composite composite = assemblyFactory.createComposite();
        composite.setURI("testComposite");
        composite.setName(new QName("testComposite"));

        // create a component
        Component component = assemblyFactory.createComponent();
        component.setName("testComponent");
        JavaImplementationFactory javaImplementationFactory = modelFactories.getFactory(JavaImplementationFactory.class);
        JavaImplementation javaImplementation = javaImplementationFactory.createJavaImplementation(contribution.getClassLoader().loadClass("sample.HelloworldFaultsImpl"));
        javaImplementation.setJavaClass(contribution.getClassLoader().loadClass("sample.HelloworldFaultsImpl"));
        component.setImplementation(javaImplementation);

        // add the component to the composite
        composite.getComponents().add(component);

        // add the composite to the contribution
        contribution.addComposite(composite);

        node.installContribution(contribution, null);
        
        node.startComposite(contribution.getURI(), composite.getURI());
    }
}
