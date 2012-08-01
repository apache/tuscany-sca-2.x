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
package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.junit.Test;

import sample.Helloworld;
import sample.HelloworldImpl;

/**
 * Same as ComponentTestCase but this additionally adds a Web service binding to the service
 * to create a WS endpoint. 
 */
public class WSServiceTestCase extends TestCase {

    private TuscanyRuntime tuscanyRuntime;
    private ExtensionPointRegistry extensionPoints;
    private FactoryExtensionPoint modelFactories;
    private Node node;

    @Override
    protected void setUp() throws Exception {
        tuscanyRuntime = TuscanyRuntime.newInstance();
        extensionPoints = tuscanyRuntime.getExtensionPointRegistry();
        modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        node = tuscanyRuntime.createNode();
    }

    @Test
    public void testInvoke() throws Exception {

        // Create a contribution
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI("testContribution");
        ModelResolverExtensionPoint modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
        contribution.setModelResolver(modelResolver);
        contribution.setClassLoader(Helloworld.class.getClassLoader());

        // Create a composite
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        Composite composite = assemblyFactory.createComposite();
        composite.setURI("testComposite");
        composite.setName(new QName("testComposite"));

        // create a component
        Component component = assemblyFactory.createComponent();
        component.setName("testComponent");        
        
        // create an implementation and set it on the component
        JavaImplementationFactory javaImplementationFactory = modelFactories.getFactory(JavaImplementationFactory.class);
        JavaImplementation javaImplementation = javaImplementationFactory.createJavaImplementation(HelloworldImpl.class);
        javaImplementation.setJavaClass(HelloworldImpl.class);
        component.setImplementation(javaImplementation);

        ComponentService cs = assemblyFactory.createComponentService();
        cs.setName("Helloworld");
        WebServiceBindingFactory webServiceBindingFactory = modelFactories.getFactory(WebServiceBindingFactory.class);
        WebServiceBinding wsBinding = webServiceBindingFactory.createWebServiceBinding();
//        PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);
//        Intent intent = policyFactory.createIntent();
//        intent.setName(new QName("http://docs.oasis-open.org/ns/opencsa/sca/200912", "SOAP.v1_1"));
//        DefaultIntent di = policyFactory.createDefaultIntent();
//        di.setIntent(intent);
//        ((DefaultingPolicySubject)b).getDefaultIntents().add(di);
        cs.getBindings().add(wsBinding);
        cs.setInterfaceContract(component.getImplementation().getService("Helloworld").getInterfaceContract());

        component.getServices().add(cs);

        // add the component to the composite
        composite.getComponents().add(component);

        // add the composite to the contribution
        contribution.addComposite(composite);

        // Now install the contribution and start the composite
        node.installContribution(contribution, null);
        node.startComposite(contribution.getURI(), composite.getURI());

        // test that has exposed an HTTP endpoint that works as expected
        // to keep this test simple just do ?wsdl on the endpoint  
        URL url = new URL("http://localhost:8080/testComponent/Helloworld?wsdl");
        Assert.assertTrue(read(url.openStream()).contains("address location="));
    }

    @Override
    protected void tearDown() throws Exception {
        node.stop();
        tuscanyRuntime.stop();
    }

    private static String read(InputStream is) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

}
