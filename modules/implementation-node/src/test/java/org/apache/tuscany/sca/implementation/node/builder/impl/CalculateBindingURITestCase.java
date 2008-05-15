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

package org.apache.tuscany.sca.implementation.node.builder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.implementation.node.NodeImplementation;
import org.apache.tuscany.sca.implementation.node.NodeImplementationFactory;
import org.apache.tuscany.sca.implementation.node.impl.NodeImplementationFactoryImpl;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 *
 * @version $Rev$ $Date$
 */
public class CalculateBindingURITestCase extends TestCase {
    private final static Logger logger = Logger.getLogger(CalculateBindingURITestCase.class.getName());
    private AssemblyFactory assemblyFactory;
    private SCABindingFactory scaBindingFactory;
    private NodeImplementationFactory nodeImplementationFactory;
    private Monitor monitor;
    private CompositeBuilder configurationBuilder;
    private List<Binding> defaultBindings = new ArrayList<Binding>();
    
    @Override
    protected void setUp() throws Exception {
        assemblyFactory = new DefaultAssemblyFactory();
        scaBindingFactory = new TestBindingFactory();
        nodeImplementationFactory = new NodeImplementationFactoryImpl();
        monitor = new Monitor() {
            public void problem(Problem problem) {
                if (problem.getSeverity() == Severity.INFO) {
                    logger.info(problem.toString());
                } else if (problem.getSeverity() == Severity.WARNING) {
                    logger.warning(problem.toString());
                } else if (problem.getSeverity() == Severity.ERROR) {
                    if (problem.getCause() != null) {
                        logger.log(Level.SEVERE, problem.toString(), problem.getCause());
                    } else {
                        logger.severe(problem.toString());
                    }
                }
            }
        };
        configurationBuilder = new NodeCompositeBuilderImpl(assemblyFactory, scaBindingFactory, null, null, monitor);
        Binding defaultBinding = new TestBindingImpl();
        defaultBinding.setURI("http://myhost:8080/root");
        defaultBindings.add(defaultBinding);
    }
    
    /**
     * Create a composite containing a node component pointing to the
     * given application composite.
     * 
     * @param composite
     * @return
     */
    private Composite nodeComposite(Composite composite) {
        Composite nodeComposite = assemblyFactory.createComposite();
        Component nodeComponent = assemblyFactory.createComponent();
        NodeImplementation nodeImplementation = nodeImplementationFactory.createNodeImplementation();
        nodeImplementation.setComposite(composite);
        nodeComponent.setImplementation(nodeImplementation);
        ComponentService nodeService = assemblyFactory.createComponentService();
        nodeService.getBindings().addAll(defaultBindings);
        nodeComponent.getServices().add(nodeService);
        nodeComposite.getComponents().add(nodeComponent);
        return nodeComposite;
    }
    
    @Override
    protected void tearDown() throws Exception {
        assemblyFactory = null;
    }
    
    /**
     * Test that URI are generated in accordance with the Assembly Specification section 1.7.2.1 as
     * follows. For the 3 parts that make up the URI;
     * 
     *   BaseURI / Component URI / Service Binding URI
     *   
     * Test the following combinations for:
     * 
     * NB. The short hand here, e.g. <service name="s1"> <binding.sca> <service name="s2"> means
     * two services appear where the first has the sca binding specified. 
     * 
     * component service bindings
     * 
     * http://myhost:8080/root  /  <component name="c1">  / <service name="s1"> <binding.sca>
     * --> http://myhost:8080/root/c1
     * http://myhost:8080/root  /  <component name="c1">  / <service name="s1"> <binding.sca> <service name="s2">
     * --> http://myhost:8080/root/c1/s1
     * http://myhost:8080/root  /  <component name="c1">  / <service name="s1"> <binding.sca name="n"> <service name="s2">
     * --> http://myhost:8080/root/c1/n
     * http://myhost:8080/root  /  <component name="c1">  / <service name="s1"> <binding.sca uri="b"> <service name="s2">
     * --> http://myhost:8080/root/c1/b
     * http://myhost:8080/root  /  <component name="c1">  / <service name="s1"> <binding.sca uri="http://myhost:8080/b"> <service name="s2">
     * --> http://myhost:8080/b
     * http://myhost:8080/root  /  <component name="c1">  / <service name="s1"> <binding.sca uri="../../b"> <service name="s2">
     * --> http://myhost:8080/b  
     * 
     * top level composite service bindings
     * 
     * http://myhost:8080/root  /  null  / <service name="s1"> <binding.sca> <service name="s2">
     * --> http://myhost:8080/root
     * http://myhost:8080/root  /  null  / <service name="s1"> <binding.sca> <service name="s2">
     * --> http://myhost:8080/root/s1
     * http://myhost:8080/root  /  null  / <service name="s1"> <binding.sca name="n"> <service name="s2">
     * --> http://myhost:8080/root/n
     * http://myhost:8080/root  /  null  / <service name="s1"> <binding.sca uri="b"> <service name="s2">
     * --> http://myhost:8080/root/b
     * http://myhost:8080/root  /  null  / <service name="s1"> <binding.sca uri="http://myhost:8080/b"> <service name="s2">
     * --> http://myhost:8080/b
     * 
     * nested composite service bindings
     * 
     * http://myhost:8080/root  /  <component name="c1"> implemented by composite with <component name="c2"> / <service name="s1"> <binding.sca>
     * --> http://myhost:8080/root/c1/c2
     * http://myhost:8080/root  /  <component name="c1"> implemented by composite with <component name="c2"> / <service name="s1"> <binding.sca> <service name="s2">
     * --> http://myhost:8080/root/c1/c2/s1
     * http://myhost:8080/root  /  <component name="c1"> implemented by composite with <component name="c2"> / <service name="s1"> <binding.sca name="n"> <service name="s2">
     * --> http://myhost:8080/root/c1/c2/n
     * http://myhost:8080/root  /  <component name="c1"> implemented by composite with <component name="c2"> / <service name="s1"> <binding.sca uri="b"> <service name="s2">
     * --> http://myhost:8080/root/c1/c2/b
     * http://myhost:8080/root  /  <component name="c1"> implemented by composite with <component name="c2"> / <service name="s1"> <binding.sca uri="http://myhost:8080/b"> <service name="s2">
     * --> http://myhost:8080/b
     * 
     * binding name duplication errors
     * 
     * http://myhost:8080/root  /  <component name="c1"> implemented by composite with <component name="c2"> / <service name="s1"> <binding.sca> <binding.xyz>
     * --> Error
     * http://myhost:8080/root  /  <component name="c1"> implemented by composite with <component name="c2"> / <service name="s1"> <binding.sca name="b1"> <binding.xyz name="b1">
     * --> Error
     */
    
    private Composite createComponentServiceBinding() {
        Composite composite1 = assemblyFactory.createComposite();
        composite1.setName(new QName("http://foo", "C1"));
        
        Component c1 = assemblyFactory.createComponent();
        c1.setName("c1");
        composite1.getComponents().add(c1);
        
        ComponentService s1 = assemblyFactory.createComponentService();
        c1.getServices().add(s1);
        s1.setName("s1");    
        
        ComponentService s2 = assemblyFactory.createComponentService();
        c1.getServices().add(s2);
        s2.setName("s2");        
        
        Binding b1 = new TestBindingImpl();
        s1.getBindings().add(b1);
        
        Binding b2 = new TestBindingImpl();
        s2.getBindings().add(b2);        
        
        return composite1;
    }
    
    private Composite createTopLevelCompositeServiceBinding(){
        Composite composite1 = assemblyFactory.createComposite();
        composite1.setName(new QName("http://foo", "C1"));
        
        CompositeService s1 = assemblyFactory.createCompositeService();
        s1.setName("s1");
        composite1.getServices().add(s1);
        
        Binding b1 = new TestBindingImpl();
        s1.getBindings().add(b1);
        
        CompositeService s2 = assemblyFactory.createCompositeService();
        s2.setName("s2");
        composite1.getServices().add(s2);
        
        Binding b2 = new TestBindingImpl();
        s2.getBindings().add(b2);
        
        return composite1;
    }
    
    private Composite createNestCompositeServiceBinding(){
        Composite composite1 = assemblyFactory.createComposite();
        composite1.setName(new QName("http://foo", "C1"));
        
        Component c1 = assemblyFactory.createComponent();
        c1.setName("c1");
        composite1.getComponents().add(c1);
        
        Composite composite2 = assemblyFactory.createComposite();
        c1.setImplementation(composite2);
        composite2.setName(new QName("http://foo", "C2"));
        
        Component c2 = assemblyFactory.createComponent();
        composite2.getComponents().add(c2);
        c2.setName("c2");
        
        ComponentService s1 = assemblyFactory.createComponentService();
        c2.getServices().add(s1);
        s1.setName("s1");    
        
        ComponentService s2 = assemblyFactory.createComponentService();
        c2.getServices().add(s2);
        s2.setName("s2");        
        
        Binding b1 = new TestBindingImpl();
        s1.getBindings().add(b1);
        
        Binding b2 = new TestBindingImpl();
        s2.getBindings().add(b2);  
        
        return composite1;       
    }
    
    // component service binding tests
   
    public void testComponentServiceSingleService() {
        Composite composite = createComponentServiceBinding();
        composite.getComponents().get(0).getServices().remove(1);
        Binding b = composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root/c1", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }
    
    public void testComponentServiceBindingDefault() {
        Composite composite = createComponentServiceBinding();
        Binding b = composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root/c1/s1", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }  
    
    public void testComponentServiceBindingName() {
        Composite composite = createComponentServiceBinding();
        Binding b = composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        b.setName("n");
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root/c1/n", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }   
    
    public void testComponentServiceBindingURIRelative() {
        Composite composite = createComponentServiceBinding();
        Binding b = composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        b.setName("n");
        b.setURI("b");
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root/c1/b", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }  
    
    public void testComponentServiceBindingURIAbsolute() {
        Composite composite = createComponentServiceBinding();
        Binding b = composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        b.setName("n");
        b.setURI("http://myhost:8080/b");
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/b", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    } 
    
    public void testComponentServiceBindingURIRelative2() {
        Composite composite = createComponentServiceBinding();
        Binding b = composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        b.setName("n");
        b.setURI("../../b");
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/b", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }     
    
    // top level composite service binding tests
    
    public void testCompositeServiceSingleService() {
        Composite composite = createTopLevelCompositeServiceBinding();
        composite.getServices().remove(1);
        Binding b = composite.getServices().get(0).getBindings().get(0);
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }
   
    public void testCompositeServiceBindingDefault() {
        Composite composite = createTopLevelCompositeServiceBinding();
        Binding b = composite.getServices().get(0).getBindings().get(0);
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root/s1", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }  
    
    public void testCompositeServiceBindingName() {
        Composite composite = createTopLevelCompositeServiceBinding();
        Binding b = composite.getServices().get(0).getBindings().get(0);
        b.setName("n");
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root/n", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }   
    
    public void testCompositeServiceBindingURIRelative() {
        Composite composite = createTopLevelCompositeServiceBinding();
        Binding b = composite.getServices().get(0).getBindings().get(0);
        b.setName("n");
        b.setURI("b");
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root/b", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }  
    
    public void testCompositeServiceBindingURIAbsolute() {
        Composite composite = createTopLevelCompositeServiceBinding();
        Binding b = composite.getServices().get(0).getBindings().get(0);
        b.setName("n");
        b.setURI("http://myhost:8080/b");
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/b", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }        

    // nested composite service binding tests
    
    public void testNestedCompositeServiceSingleService() {
        Composite composite = createNestCompositeServiceBinding();
        ((Composite)composite.getComponents().get(0).getImplementation()).getComponents().get(0).getServices().remove(1);
        Binding b = ((Composite)composite.getComponents().get(0).getImplementation()).getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root/c1/c2", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }
    
    public void testNestedCompositeServiceBindingDefault() {
        Composite composite = createNestCompositeServiceBinding();
        Binding b = ((Composite)composite.getComponents().get(0).getImplementation()).getComponents().get(0).getServices().get(0).getBindings().get(0);
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root/c1/c2/s1", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }  
    
    public void testNestedCompositeServiceBindingName() {
        Composite composite = createNestCompositeServiceBinding();
        Binding b = ((Composite)composite.getComponents().get(0).getImplementation()).getComponents().get(0).getServices().get(0).getBindings().get(0);
        b.setName("n");
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root/c1/c2/n", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }   
    
    public void testNestedCompositeServiceBindingURIRelative() {
        Composite composite = createNestCompositeServiceBinding();
        Binding b = ((Composite)composite.getComponents().get(0).getImplementation()).getComponents().get(0).getServices().get(0).getBindings().get(0);
        b.setName("n");
        b.setURI("b");
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/root/c1/c2/b", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }  
    
    public void testNestedCompositeServiceBindingURIAbsolute() {
        Composite composite = createNestCompositeServiceBinding();
        Binding b = ((Composite)composite.getComponents().get(0).getImplementation()).getComponents().get(0).getServices().get(0).getBindings().get(0);
        b.setName("n");
        b.setURI("http://myhost:8080/b");
        
        try {
            configurationBuilder.build(nodeComposite(composite));

            assertEquals("http://myhost:8080/b", b.getURI());
        } catch(Exception ex){
            System.out.println(ex.toString());
            fail();
        }  
    }   
    
    // component service binding name error tests
    
    //FIXME Need to find a better way to test these error cases as
    // the composite builder now (intentionally) logs warnings instead of 
    // throwing exceptions
    public void FIXMEtestComponentServiceBindingNameError1() {
        Composite composite = createComponentServiceBinding();
        Binding b1 = composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        Binding b2 = new TestBindingImpl();
        composite.getComponents().get(0).getServices().get(0).getBindings().add(b2);
        
        
        try {
            configurationBuilder.build(nodeComposite(composite));
            fail();
        } catch(Exception ex){
            //System.out.println(ex.toString());
        }  
    }
    
    //FIXME Need to find a better way to test these error cases as
    // the composite builder now (intentionally) logs warnings instead of 
    // throwing exceptions
    public void FIXMEtestComponentServiceBindingNameError2() {
        Composite composite = createComponentServiceBinding();
        Binding b1 = composite.getComponents().get(0).getServices().get(0).getBindings().get(0);
        Binding b2 = new TestBindingImpl();
        composite.getComponents().get(0).getServices().get(0).getBindings().add(b2);
        
        b1.setName("b");
        b2.setName("b");
        
        
        try {
            configurationBuilder.build(nodeComposite(composite));
            fail();
        } catch(Exception ex){
            System.out.println(ex.toString());
        }  
    }    

    
    public class TestBindingFactory implements SCABindingFactory {
        public SCABinding createSCABinding() {
            return new TestBindingImpl();
        }
    }
    
    public class TestBindingImpl implements SCABinding {
        private String name;
        private String uri;
        private boolean unresolved;
       
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getURI() {
            return uri;
        }

        public void setURI(String uri) {
            this.uri = uri;
        }
        
        public void setUnresolved(boolean unresolved) {
            this.unresolved = unresolved;
        }
        
        public boolean isUnresolved() {
            return unresolved;
        }
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

}
