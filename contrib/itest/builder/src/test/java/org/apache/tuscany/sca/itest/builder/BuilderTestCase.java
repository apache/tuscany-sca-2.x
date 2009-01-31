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
package org.apache.tuscany.sca.itest.builder;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;

/**
 * Load and build some composites and inspect the results.
 * 
 * @version $Rev$ $Date$
 */
public class BuilderTestCase extends TestCase {
    private CustomCompositeBuilder customBuilder;
    
    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }


    // Scenario 1: <binding.ws> on outer composite service CompositeA/Service1
    public void testScenario1() throws Exception {
        System.out.println("====>Running testScenario1");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario1.composite", "TestContribution", "src/main/resources/scenario1/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario1Results(false);
    }

    public void testScenario1NonWiring() throws Exception {
        System.out.println("====>Running testScenario1NonWiring");
        customBuilder = new CustomCompositeBuilder(true);
        customBuilder.loadContribution("scenario1.composite", "TestContribution", "src/main/resources/scenario1/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario1Results(true);
    }

    private void checkScenario1Results(boolean nonWiring) {
        Composite domainComposite = customBuilder.getDomainComposite();

        Component componentD = TestUtils.getComponent(domainComposite, "ComponentD");
        if (!nonWiring) {
            // Should create component service $promoted$Service1 on innermost component
            //  ComponentD, with <binding.ws> and uri="/Service1"
            // No other services on ComponentD should have <binding.ws>
            WebServiceBinding wsBinding = null;
            for (ComponentService service : componentD.getServices()) {
                WebServiceBinding wsb = service.getBinding(WebServiceBinding.class);
                if ("$promoted$Service1".equals(service.getName())) {
                    wsBinding = wsb;
                } else {
                    assert wsb == null;
                }
            }
            assert "/Service1".equals(wsBinding.getURI());

            // Should create WSDL document for ComponentD/$promoted$Service1 with endpoint uri="/Service1"
            Definition def = wsBinding.getWSDLDocument();
            javax.wsdl.Service svc = def.getService(new QName("http://builder.itest.sca.tuscany.apache.org/", "Service3Service")); 
            Port port = svc.getPort("Service3Port");
            assert "/Service1".equals(TestUtils.getPortAddress(port));
        } else {
            // Should not create component service $promoted$Service1 on innermost component ComponentD
            // No component services on ComponentD should have <binding.ws>
            for (ComponentService service : componentD.getServices()) {
                assert !"$promoted$Service1".equals(service.getName());
                assert service.getBinding(WebServiceBinding.class) == null;
            }
        }

        // No services on ComponentB should have <binding.ws>
        Component componentB = TestUtils.getComponent(domainComposite, "ComponentB");
        for (ComponentService service : componentB.getServices()) {
            assert service.getBinding(WebServiceBinding.class) == null;
        }

        // No services on CompositeC should have <binding.ws>
        Composite compositeC = TestUtils.getComposite(domainComposite, new QName("http://scenario1", "CompositeC"));
        for (Service service : compositeC.getServices()) {
            assert service.getBinding(WebServiceBinding.class) == null;
        }

        if (nonWiring) {
            // Should not add a WSDL document to domain composite service Service1
            WebServiceBinding wsBinding = null;
            for (Service service : domainComposite.getServices()) {
                if ("Service1".equals(service.getName())) {
                    wsBinding = service.getBinding(WebServiceBinding.class);
                }
            }
            assert wsBinding.getWSDLDocument() == null;
        }
    }

    // Scenario 2: <binding.ws> on outer component service ComponentB/Service2
    public void testScenario2() throws Exception {
        System.out.println("====>Running testScenario2");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario2.composite", "TestContribution", "src/main/resources/scenario2/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario2and3Results("http://scenario2", false);
    }

    public void testScenario2NonWiring() throws Exception {
        System.out.println("====>Running testScenario2NonWiring");
        customBuilder = new CustomCompositeBuilder(true);
        customBuilder.loadContribution("scenario2.composite", "TestContribution", "src/main/resources/scenario2/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario2and3Results("http://scenario2", true);
    }

    private void checkScenario2and3Results(String namespace, boolean nonWiring) {
        Composite domainComposite = customBuilder.getDomainComposite();

        // Should create WSDL document for ComponentB/Service2 with endpoint uri="/ComponentB/Service2"
        // No other services on ComponentB should have <binding.ws>
        Component componentB = TestUtils.getComponent(domainComposite, "ComponentB");
        WebServiceBinding wsBinding = null;
        for (ComponentService service : componentB.getServices()) {
            WebServiceBinding wsb = service.getBinding(WebServiceBinding.class);
            if ("Service2".equals(service.getName())) {
                wsBinding = wsb;
            } else {
                assert wsb == null;
            }
        }
        Definition def = wsBinding.getWSDLDocument();
        javax.wsdl.Service svc = def.getService(new QName("http://builder.itest.sca.tuscany.apache.org/", "Service3Service")); 
        Port port = svc.getPort("Service3Port");
        assert "/ComponentB/Service2".equals(TestUtils.getPortAddress(port));

        Component componentD = TestUtils.getComponent(domainComposite, "ComponentD");
        if (!nonWiring) {
            // Should create component service $promoted$ComponentB$slash$Service2 on innermost component
            //  ComponentD, with <binding.ws> and uri="/ComponentB/Service2"
            wsBinding = null;
            for (ComponentService service : componentD.getServices()) {
                if ("$promoted$ComponentB$slash$Service2".equals(service.getName())) {
                    wsBinding = service.getBinding(WebServiceBinding.class);
                }
            }
            assert "/ComponentB/Service2".equals(wsBinding.getURI());

            // Should create WSDL document for ComponentD/$promoted$ComponentB$slash$Service2 with endpoint uri="/ComponentB/Service2"
            def = wsBinding.getWSDLDocument();
            svc = def.getService(new QName("http://builder.itest.sca.tuscany.apache.org/", "Service3Service")); 
            port = svc.getPort("Service3Port");
            assert "/ComponentB/Service2".equals(TestUtils.getPortAddress(port));
        } else {
            // Should not create component service $promoted$ComponentB$slash$Service2 on innermost component ComponentD
            for (ComponentService service : componentD.getServices()) {
                assert !"$promoted$ComponentB$slash$Service2".equals(service.getName());
            }
        }

        // Should add <binding.ws> to outer composite service CompositeA/Service1 
        wsBinding = null;
        for (Service service : domainComposite.getServices()) {
            if ("Service1".equals(service.getName())) {
                wsBinding = service.getBinding(WebServiceBinding.class);
            }
        }
        assert wsBinding != null;
        if (nonWiring) {
            // Should not add a WSDL document to domain composite service Service1
            assert wsBinding.getWSDLDocument() == null;
        }

        if (!nonWiring) {
            // Should create component service $promoted$Service1 on innermost component
            //  ComponentD, with <binding.ws> and uri="/Service1"
            wsBinding = null;
            for (ComponentService service : componentD.getServices()) {
                if ("$promoted$Service1".equals(service.getName())) {
                    wsBinding = service.getBinding(WebServiceBinding.class);
                }
            }
            assert "/Service1".equals(wsBinding.getURI());

            // Should create WSDL document for ComponentD/$promoted$Service1 with endpoint uri="/Service1"
            def = wsBinding.getWSDLDocument();
            svc = def.getService(new QName("http://builder.itest.sca.tuscany.apache.org/", "Service3Service")); 
            port = svc.getPort("Service3Port");
            assert "/Service1".equals(TestUtils.getPortAddress(port));
        } else {
            // Should not create component service $promoted$.Service1 on innermost component ComponentD
            for (ComponentService service : componentD.getServices()) {
                assert !"$promoted$Service1".equals(service.getName());
            }
        }

        // No services on ComponentD should have <binding.ws>, except for $promoted$Service1
        // and $promoted$ComponentB$slash$Service2  
        for (ComponentService service : componentD.getServices()) {
            if (!"$promoted$Service1".equals(service.getName()) &&
                !"$promoted$ComponentB$slash$Service2".equals(service.getName())) {
                assert service.getBinding(WebServiceBinding.class) == null;
            }
        }

        // No services on CompositeC should have <binding.ws>, except for Service2 in Scenario 3
        Composite compositeC = TestUtils.getComposite(domainComposite, new QName(namespace, "CompositeC"));
        for (Service service : compositeC.getServices()) {
            if ("http://scenario3".equals(namespace) && "Service2".equals(service.getName())) {
                assert service.getBinding(WebServiceBinding.class) != null;
            } else {
                assert service.getBinding(WebServiceBinding.class) == null;
            }
        }
    }

    // Scenario 3: <binding.ws> on inner composite service CompositeC/Service2
    public void testScenario3() throws Exception {
        System.out.println("====>Running testScenario3");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario3.composite", "TestContribution", "src/main/resources/scenario3/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario2and3Results("http://scenario3", false);
    }

    public void testScenario3NonWiring() throws Exception {
        System.out.println("====>Running testScenario3NonWiring");
        customBuilder = new CustomCompositeBuilder(true);
        customBuilder.loadContribution("scenario3.composite", "TestContribution", "src/main/resources/scenario3/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario2and3Results("http://scenario3", true);
    }

    // Scenario 4: <binding.ws> on inner component service ComponentD/Service3
    public void testScenario4() throws Exception {
        System.out.println("====>Running testScenario4");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario4.composite", "TestContribution", "src/main/resources/scenario4/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario4Results(false);
    }

    public void testScenario4NonWiring() throws Exception {
        System.out.println("====>Running testScenario4NonWiring");
        customBuilder = new CustomCompositeBuilder(true);
        customBuilder.loadContribution("scenario4.composite", "TestContribution", "src/main/resources/scenario4/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario4Results(true);
    }

    private void checkScenario4Results(boolean nonWiring) {
        Composite domainComposite = customBuilder.getDomainComposite();

        // Should create WSDL document for ComponentD/Service3 with endpoint uri="/ComponentD/Service3"
        Component componentD = TestUtils.getComponent(domainComposite, "ComponentD");
        WebServiceBinding wsBinding = null;
        for (ComponentService service : componentD.getServices()) {
            if ("Service3".equals(service.getName())) {
                wsBinding = service.getBinding(WebServiceBinding.class);
            }
        }
        Definition def = wsBinding.getWSDLDocument();
        javax.wsdl.Service svc = def.getService(new QName("http://builder.itest.sca.tuscany.apache.org/", "Service3Service")); 
        Port port = svc.getPort("Service3Port");
        assert "/ComponentB/ComponentD/Service3".equals(TestUtils.getPortAddress(port));

        // Should add <binding.ws> to inner composite service CompositeC/Service2 
        // No other services on CompositeC should have <binding.ws>
        Composite compositeC = TestUtils.getComposite(domainComposite, new QName("http://scenario4", "CompositeC"));
        wsBinding = null;
        for (Service service : compositeC.getServices()) {
            WebServiceBinding wsb = service.getBinding(WebServiceBinding.class);
            if ("Service2".equals(service.getName())) {
                wsBinding = wsb;
            } else {
                assert wsb == null;
            }
        }
        assert "/ComponentB/Service2".equals(wsBinding.getURI());

        // Should add <binding.ws> to outer component service ComponentB/Service2 
        // Should create WSDL document for ComponentB/Service2 with endpoint uri="/ComponentB/Service2"
        // No other services on ComponentB should have <binding.ws>
        Component componentB = TestUtils.getComponent(domainComposite, "ComponentB");
        wsBinding = null;
        for (ComponentService service : componentB.getServices()) {
            WebServiceBinding wsb = service.getBinding(WebServiceBinding.class);
            if ("Service2".equals(service.getName())) {
                wsBinding = wsb;
            } else {
                assert wsb == null;
            }
        }
        assert "/ComponentB/Service2".equals(wsBinding.getURI());
        def = wsBinding.getWSDLDocument();
        svc = def.getService(new QName("http://builder.itest.sca.tuscany.apache.org/", "Service3Service")); 
        port = svc.getPort("Service3Port");
        assert "/ComponentB/Service2".equals(TestUtils.getPortAddress(port));

        if (!nonWiring) {
            // Should create component service $promoted$ComponentB$slash$Service2 on innermost component
            //  ComponentD, with <binding.ws> and uri="/ComponentB/Service2"
            wsBinding = null;
            for (ComponentService service : componentD.getServices()) {
                if ("$promoted$ComponentB$slash$Service2".equals(service.getName())) {
                    wsBinding = service.getBinding(WebServiceBinding.class);
                }
            }
            assert "/ComponentB/Service2".equals(wsBinding.getURI());

            // Should create WSDL document for ComponentD/$promoted$ComponentB$slash$Service2 with endpoint uri="/ComponentB/Service2"
            def = wsBinding.getWSDLDocument();
            svc = def.getService(new QName("http://builder.itest.sca.tuscany.apache.org/", "Service3Service")); 
            port = svc.getPort("Service3Port");
            assert "/ComponentB/Service2".equals(TestUtils.getPortAddress(port));
        } else {
            // Should not create component service $promoted$ComponentB$slash$Service2 on innermost component ComponentD
            for (ComponentService service : componentD.getServices()) {
                assert !"$promoted$ComponentB$slash$Service2".equals(service.getName());
            }
        }

        // Should add <binding.ws> to outer composite service CompositeA/Service1 
        wsBinding = null;
        for (Service service : domainComposite.getServices()) {
            if ("Service1".equals(service.getName())) {
                wsBinding = service.getBinding(WebServiceBinding.class);
            }
        }
        assert wsBinding != null;
        if (nonWiring) {
            // Should not add a WSDL document to domain composite service Service1
            assert wsBinding.getWSDLDocument() == null;
        }

        if (!nonWiring) {
            // Should create component service $promoted$Service1 on innermost component
            //  ComponentD, with <binding.ws> and uri="/Service1"
            wsBinding = null;
            for (ComponentService service : componentD.getServices()) {
                if ("$promoted$Service1".equals(service.getName())) {
                    wsBinding = service.getBinding(WebServiceBinding.class);
                }
            }
            assert "/Service1".equals(wsBinding.getURI());

            // Should create WSDL document for ComponentD/$promoted$Service1 with endpoint uri="/Service1"
            def = wsBinding.getWSDLDocument();
            svc = def.getService(new QName("http://builder.itest.sca.tuscany.apache.org/", "Service3Service")); 
            port = svc.getPort("Service3Port");
            assert "/Service1".equals(TestUtils.getPortAddress(port));
        } else {
            // Should not create component service $promoted$.Service1 on innermost component ComponentD
            for (ComponentService service : componentD.getServices()) {
                assert !"$promoted$Service1".equals(service.getName());
            }
        }

        // No services on ComponentD should have <binding.ws>, except for Service3,
        //  $promoted$.Service1 and $promoted$.ComponentB.Service2
        for (ComponentService service : componentD.getServices()) {
            if (!"Service3".equals(service.getName()) &&
                !"$promoted$Service1".equals(service.getName()) &&
                !"$promoted$ComponentB$slash$Service2".equals(service.getName())) {
                assert service.getBinding(WebServiceBinding.class) == null;
            }
        }
    }

    // Scenario 5: <binding.ws> and <interface.wsdl> on outer composite reference CompositeA/reference1
    public void testScenario5() throws Exception {
        System.out.println("====>Running testScenario5");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario5.composite", "TestContribution", "src/main/resources/scenario5/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario5Results(false);
    }

    public void testScenario5NonWiring() throws Exception {
        System.out.println("====>Running testScenario5NonWiring");
        customBuilder = new CustomCompositeBuilder(true);
        customBuilder.loadContribution("scenario5.composite", "TestContribution", "src/main/resources/scenario5/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario5Results(true);
    }

    private void checkScenario5Results(boolean nonWiring) {
        Composite domainComposite = customBuilder.getDomainComposite();

        // Should not add <binding.ws> to any outer component references 
        Component componentB = TestUtils.getComponent(domainComposite, "ComponentB");
        for (ComponentReference reference : componentB.getReferences()) {
            assert reference.getBinding(WebServiceBinding.class) == null;
        }

        Definition def;
        javax.wsdl.Service svc;
        Port port;
        WebServiceBinding wsBinding;
        Component componentD = TestUtils.getComponent(domainComposite, "ComponentD");
        if (!nonWiring) {
            // Should add <binding.ws> to inner component reference ComponentD/reference3 with uri="http://foo.com/bar" 
            // Should set <interface.wsdl> on inner component reference ComponentD/reference3 
            wsBinding = null;
            for (ComponentReference reference : componentD.getReferences()) {
                if ("reference3".equals(reference.getName())) {
                    wsBinding = reference.getBinding(WebServiceBinding.class);
                    assert reference.getInterfaceContract(wsBinding) instanceof WSDLInterfaceContract;
                }
            }
            assert "http://foo.com/bar".equals(wsBinding.getURI());

            // Should create WSDL document for ComponentD/reference3 with endpoint uri="http://foo.com/bar"
            def = wsBinding.getWSDLDocument();
            svc = def.getService(new QName("http://scenarios/ComponentD/reference3", "Service3aService")); 
            port = svc.getPort("Service3aPort");
            assert "http://foo.com/bar".equals(TestUtils.getPortAddress(port));
        } else {
            // Should not add <binding.ws> to any inner component references 
            for (ComponentReference reference : componentD.getReferences()) {
                assert reference.getBinding(WebServiceBinding.class) == null;
            }
        }

        // Should not add <binding.ws> or <interface.wsdl> to inner composite reference CompositeC/reference2 
        Composite compositeC = TestUtils.getComposite(domainComposite, new QName("http://scenario5", "CompositeC"));
        for (Reference reference : compositeC.getReferences()) {
            assert reference.getBinding(WebServiceBinding.class) == null;
            assert reference.getInterfaceContract() instanceof JavaInterfaceContract;
        }
    }

    // Scenario 6: <binding.ws> and <interface.wsdl> on outer component reference ComponentB/reference2
    public void testScenario6() throws Exception {
        System.out.println("====>Running testScenario6");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario6.composite", "TestContribution", "src/main/resources/scenario6/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario6and7Results("http://scenario6", false);
    }

    public void testScenario6NonWiring() throws Exception {
        System.out.println("====>Running testScenario6NonWiring");
        customBuilder = new CustomCompositeBuilder(true);
        customBuilder.loadContribution("scenario6.composite", "TestContribution", "src/main/resources/scenario6/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario6and7Results("http://scenario6", true);
    }

    private void checkScenario6and7Results(String namespace, boolean nonWiring) {
        Composite domainComposite = customBuilder.getDomainComposite();

        // Should remove target= on ComponentB/reference2 (for Scenario 6) or
        //  CompositeC/reference2 (for Scenario 7), and add uri="http://foo.com/bar"
        //  to the <binding.ws> element on ComponentB/reference2
        // For nonWiring, ComponentB/reference2 should have target=
        //  and no uri= should be added
        Component componentB = TestUtils.getComponent(domainComposite, "ComponentB");
        WebServiceBinding wsBinding = null;
        for (ComponentReference reference : componentB.getReferences()) {
            if ("reference2".equals(reference.getName())) {
                if (!nonWiring) {
                    assert reference.getTargets().size() == 0;
                } else {
                    assert reference.getTargets().size() == 1;
                }
                wsBinding = reference.getBinding(WebServiceBinding.class);
            }
        }
        if (!nonWiring) {
            assert "http://foo.com/bar".equals(wsBinding.getURI());
        } else {
            assert wsBinding.getURI() == null;
        }

        Definition def;
        javax.wsdl.Service svc;
        Port port;
        if (!nonWiring) {
            // Should create WSDL document for ComponentB/reference2 with endpoint uri="http://foo.com/bar"
            def = wsBinding.getWSDLDocument();
            svc = def.getService(new QName("http://scenarios/ComponentB/reference2", "Service3aService")); 
            port = svc.getPort("Service3aPort");
            assert "http://foo.com/bar".equals(TestUtils.getPortAddress(port));
        }

        Component componentD = TestUtils.getComponent(domainComposite, "ComponentD");
        if (!nonWiring) {
            // Should add <binding.ws> to inner component reference ComponentD/reference3 with uri="http://foo.com/bar" 
            // Should set <interface.wsdl> on inner component reference ComponentD/reference3 
            wsBinding = null;
            for (ComponentReference reference : componentD.getReferences()) {
                if ("reference3".equals(reference.getName())) {
                    wsBinding = reference.getBinding(WebServiceBinding.class);
                    assert reference.getBindings().size() == 1;
                    assert reference.getInterfaceContract(wsBinding) instanceof WSDLInterfaceContract;
                }
            }
            assert "http://foo.com/bar".equals(wsBinding.getURI());

            // Should create WSDL document for ComponentD/reference3 with endpoint uri="http://foo.com/bar"
            def = wsBinding.getWSDLDocument();
            svc = def.getService(new QName("http://scenarios/ComponentB/reference2", "Service3aService")); 
            port = svc.getPort("Service3aPort");
            assert "http://foo.com/bar".equals(TestUtils.getPortAddress(port));
        } else {
            // Should not add <binding.ws> to any inner component references 
            for (ComponentReference reference : componentD.getReferences()) {
                assert reference.getBinding(WebServiceBinding.class) == null;
            }
        }

        // No references on CompositeC should have <binding.ws> or <interface.wsdl>, except for
        //  reference2 in Scenario 7
        Composite compositeC = TestUtils.getComposite(domainComposite, new QName(namespace, "CompositeC"));
        for (Reference reference : compositeC.getReferences()) {
            if ("http://scenario7".equals(namespace) && "reference2".equals(reference.getName())) {
                assert reference.getBinding(WebServiceBinding.class)!= null;
                assert reference.getInterfaceContract() instanceof WSDLInterfaceContract;
            } else {
                assert reference.getBinding(WebServiceBinding.class) == null;
                assert reference.getInterfaceContract() instanceof JavaInterfaceContract;
            }
        }

        // Should add <binding.ws> and <interface.wsdl> to outer composite reference CompositeA/reference1 
        wsBinding = null;
        for (Reference reference : domainComposite.getReferences()) {
            if ("reference1".equals(reference.getName())) {
                wsBinding = reference.getBinding(WebServiceBinding.class);
                assert reference.getInterfaceContract() instanceof WSDLInterfaceContract;
            }
        }
        assert wsBinding.getURI() == null;

        // Should not add a WSDL document to domain composite reference reference1
        assert wsBinding.getWSDLDocument() == null;
    }

    // Scenario 7: <binding.ws> and <interface.wsdl> on inner composite reference CompositeC/reference2
    public void testScenario7() throws Exception {
        System.out.println("====>Running testScenario7");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario7.composite", "TestContribution", "src/main/resources/scenario7/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario6and7Results("http://scenario7", false);
    }

    public void testScenario7NonWiring() throws Exception {
        System.out.println("====>Running testScenario7NonWiring");
        customBuilder = new CustomCompositeBuilder(true);
        customBuilder.loadContribution("scenario7.composite", "TestContribution", "src/main/resources/scenario7/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario6and7Results("http://scenario7", true);
    }

    // Scenario 8: <binding.ws> and <interface.wsdl> on inner component reference ComponentD/reference3
    public void testScenario8() throws Exception {
        System.out.println("====>Running testScenario8");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario8.composite", "TestContribution", "src/main/resources/scenario8/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario8Results(false);
    }

    public void testScenario8NonWiring() throws Exception {
        System.out.println("====>Running testScenario8NonWiring");
        customBuilder = new CustomCompositeBuilder(true);
        customBuilder.loadContribution("scenario8.composite", "TestContribution", "src/main/resources/scenario8/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario8Results(true);
    }

    private void checkScenario8Results(boolean nonWiring) {
        Composite domainComposite = customBuilder.getDomainComposite();

        // Should replace target= on ComponentD/reference3 by uri="http://foo.com/bar" on <binding.ws>
        // For nonWiring, the original target= is preserved and there is no uri=
        Component componentD = TestUtils.getComponent(domainComposite, "ComponentD");
        WebServiceBinding wsBinding = null;
        for (ComponentReference reference : componentD.getReferences()) {
            if ("reference3".equals(reference.getName())) {
                if (!nonWiring) {
                    assert reference.getTargets().size() == 0;
                } else {
                    assert reference.getTargets().size() == 1;
                }
                wsBinding = reference.getBinding(WebServiceBinding.class);
            }
        }
        if (!nonWiring) {
            assert "http://foo.com/bar".equals(wsBinding.getURI());
        } else {
            assert wsBinding.getURI() == null;
        }

        Definition def;
        javax.wsdl.Service svc;
        Port port;
        if (!nonWiring) {
            // Should create WSDL document for ComponentD/reference3 with endpoint uri="http://foo.com/bar"
            def = wsBinding.getWSDLDocument();
            svc = def.getService(new QName("http://scenarios/ComponentD/reference3", "Service3aService")); 
            port = svc.getPort("Service3aPort");
            assert "http://foo.com/bar".equals(TestUtils.getPortAddress(port));
        }

        // Should add <binding.ws> and <interface.wsdl> to inner composite reference CompositeC/reference2 
        Composite compositeC = TestUtils.getComposite(domainComposite, new QName("http://scenario8", "CompositeC"));
        wsBinding = null;
        for (Reference reference : compositeC.getReferences()) {
            if ("reference2".equals(reference.getName())) {
                wsBinding = reference.getBinding(WebServiceBinding.class);
                assert reference.getInterfaceContract() instanceof WSDLInterfaceContract;
            }
        }
        assert wsBinding.getURI() == null;

        // Should add <binding.ws> and <interface.wsdl> to outer component reference ComponentB/reference2 
        Component componentB = TestUtils.getComponent(domainComposite, "ComponentB");
        wsBinding = null;
        for (ComponentReference reference : componentB.getReferences()) {
            if ("reference2".equals(reference.getName())) {
                wsBinding = reference.getBinding(WebServiceBinding.class);
                assert reference.getInterfaceContract() instanceof WSDLInterfaceContract;
            }
        }
        assert wsBinding.getURI() == null;

        // Should add <binding.ws> and <interface.wsdl> to outer composite reference CompositeA/reference1 
        wsBinding = null;
        for (Reference reference : domainComposite.getReferences()) {
            if ("reference1".equals(reference.getName())) {
                wsBinding = reference.getBinding(WebServiceBinding.class);
                assert reference.getInterfaceContract() instanceof WSDLInterfaceContract;
            }
        }
        assert wsBinding.getURI() == null;

        // Should not add a WSDL document to domain composite reference reference1
        assert wsBinding.getWSDLDocument() == null;
    }

    // Scenario 9: target in reference CDR3A and binding.ws uri= at CAR1A
    public void testScenario9() throws Exception {
        System.out.println("====>Running testScenario9");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario9.composite", "TestContribution", "src/main/resources/scenario9/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario9Results();
    }    

    private void checkScenario9Results() {
        Composite domainComposite = customBuilder.getDomainComposite();

        Component componentD = TestUtils.getComponent(domainComposite, "ComponentD");
        ComponentReference componentRef = null;
        for (ComponentReference reference : componentD.getReferences()) {
            if ("reference3a".equals(reference.getName())) {
                componentRef = reference;
                assertTrue(reference.getBindings().size() == 2);
                assertTrue(reference.getBindings().get(0) instanceof SCABinding);
                assertTrue(reference.getBindings().get(1) instanceof WebServiceBinding);
            }
        }
        assertTrue(componentRef != null);
    }    
    
    // Scenario 10: targets in references CBR2A and CDR3A and binding.ws at CBR2A
    public void testScenario10() throws Exception {
        System.out.println("====>Running testScenario10");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario10.composite", "TestContribution", "src/main/resources/scenario10/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario10And11Results();
    }    
    
    // Scenario 11: targets in references CBR2A and CDR3A and binding.ws at CCR2A
    public void testScenario11() throws Exception {
        System.out.println("====>Running testScenario11");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario11.composite", "TestContribution", "src/main/resources/scenario11/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario10And11Results();
    }

    private void checkScenario10And11Results() {
        Composite domainComposite = customBuilder.getDomainComposite();

        Component componentD = TestUtils.getComponent(domainComposite, "ComponentD");
        ComponentReference componentRef = null;
        for (ComponentReference reference : componentD.getReferences()) {
            if ("reference3a".equals(reference.getName())) {
                componentRef = reference;
                assertTrue(reference.getBindings().size() == 2);
                Binding binding1 = reference.getBindings().get(0);
                assertTrue(binding1 instanceof SCABinding);
                assertTrue(reference.getInterfaceContract(binding1) instanceof JavaInterfaceContract);
                Binding binding2 = reference.getBindings().get(1);
                assertTrue(binding2 instanceof WebServiceBinding);
                assertTrue(reference.getInterfaceContract(binding2) instanceof WSDLInterfaceContract);
            }
        }
        assertTrue(componentRef != null);
    }  

    // Scenario 12: targets in references CBR2A and CDR3A and binding.ws at CDR3A
    public void testScenario12() throws Exception {
        System.out.println("====>Running testScenario12");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario12.composite", "TestContribution", "src/main/resources/scenario12/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario12Results();
    }

    private void checkScenario12Results() {
        Composite domainComposite = customBuilder.getDomainComposite();

        Component componentD = TestUtils.getComponent(domainComposite, "ComponentD");
        ComponentReference componentRef = null;
        for (ComponentReference reference : componentD.getReferences()) {
            if ("reference3a".equals(reference.getName())) {
                componentRef = reference;
                assertTrue(reference.getBindings().size() == 2);
                assertTrue(reference.getBindings().get(0) instanceof WebServiceBinding);
                assertTrue(reference.getBindings().get(1) instanceof WebServiceBinding);
            }
        }
        assertTrue(componentRef != null);
    }

    // Scenario 13: target in reference CDR3A
    public void testScenario13() throws Exception {
        System.out.println("====>Running testScenario13");
        customBuilder = new CustomCompositeBuilder(false);
        customBuilder.loadContribution("scenario13.composite", "TestContribution", "src/main/resources/scenario13/");
        //TestUtils.printResults(customBuilder);
        TestUtils.checkProblems(customBuilder);
        checkScenario13Results();
    }

    private void checkScenario13Results() {
        Composite domainComposite = customBuilder.getDomainComposite();

        Component componentD = TestUtils.getComponent(domainComposite, "ComponentD");
        ComponentReference componentRef = null;
        for (ComponentReference reference : componentD.getReferences()) {
            if ("reference3a".equals(reference.getName())) {
                componentRef = reference;
                assertTrue(reference.getBindings().size() == 1);
                assertTrue(reference.getBindings().get(0) instanceof WebServiceBinding);
            }
        }
        assertTrue(componentRef != null);
    }

}
