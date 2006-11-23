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

package org.apache.tuscany.core.implementation.composite;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.loader.InvalidWireException;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.model.WireDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.java.JavaImplementation;

/**
 * This class tests the wire resolution function of the composite loader
 */
public class CompositeLoaderWireResolutionTestCase extends TestCase {
    private CompositeComponentType composite;
    private CompositeLoader compositeLoader = new CompositeLoader(null, null);

    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        composite = new CompositeComponentType();
        composite.setName("TestComposite");
        //add a service to the composite
        ServiceDefinition serviceDefn = new ServiceDefinition("compositeService1", null, true);
        BindlessServiceDefinition bindlessSvcDefn = new BindlessServiceDefinition("bindlessSvc", null, true, null);
        BoundServiceDefinition boundSvcDefn = new BoundServiceDefinition("boundSvc", null, true, null, null);
        BoundServiceDefinition boundSvcDefnWithTarget =
            new BoundServiceDefinition("boundSvcWithTarget", null, true, null, new URI("orgTarget"));
        composite.add(serviceDefn);
        composite.add(boundSvcDefn);
        composite.add(bindlessSvcDefn);
        composite.add(boundSvcDefnWithTarget);

        ReferenceDefinition compositeReference = new ReferenceDefinition("compositeReference", null);
        composite.add(compositeReference);

        PojoComponentType pojoComponentType1 = new PojoComponentType();
        ServiceDefinition pojoSvc1 = new ServiceDefinition("pojoSvc1", null, false);
        pojoComponentType1.add(pojoSvc1);
        ReferenceDefinition pojoRef1 = new ReferenceDefinition("pojoRef1", null);
        pojoComponentType1.add(pojoRef1);
        JavaImplementation pojoImpl1 = new JavaImplementation();
        pojoImpl1.setComponentType(pojoComponentType1);

        ComponentDefinition component1 = new ComponentDefinition("Component1", pojoImpl1);
        composite.add(component1);

        PojoComponentType pojoComponentType2 = new PojoComponentType();
        ServiceDefinition pojoSvc2 = new ServiceDefinition("pojoSvc2", null, false);
        pojoComponentType2.add(pojoSvc2);
        ServiceDefinition pojoSvc3 = new ServiceDefinition("pojoSvc3", null, false);
        pojoComponentType2.add(pojoSvc3);
        ReferenceDefinition pojoRef2 = new ReferenceDefinition("pojoRef2", null);
        pojoComponentType2.add(pojoRef2);
        ReferenceDefinition pojoRef3 = new ReferenceDefinition("pojoRef3", null);
        pojoComponentType2.add(pojoRef3);
        JavaImplementation pojoImpl2 = new JavaImplementation();
        pojoImpl2.setComponentType(pojoComponentType2);

        ComponentDefinition component2 = new ComponentDefinition("Component2", pojoImpl2);
        composite.add(component2);
    }

    @SuppressWarnings("unchecked")
    public void testCompositeSvc2CompositeReferenceWire() throws Exception {
        WireDefinition wireDefn = new WireDefinition();
        wireDefn.setSource(new URI("compositeService1"));
        wireDefn.setTarget(new URI("compositeReference"));
        composite.add(wireDefn);
        compositeLoader.resolveWires(composite);
    }

    @SuppressWarnings("unchecked")
    public void testCompositeSvc2ComponentValid() throws Exception {
        //undefined source and targets
        WireDefinition wireDefn = new WireDefinition();
        wireDefn.setSource(new URI("compositeService1"));
        wireDefn.setTarget(new URI("Component1"));
        composite.add(wireDefn);
        compositeLoader.resolveWires(composite);
    }

    @SuppressWarnings("unchecked")
    public void testCompositeSvc2ComponentQualifiedValid() throws Exception {
        //undefined source and targets
        WireDefinition wireDefn = new WireDefinition();
        wireDefn.setSource(new URI("compositeService1"));
        wireDefn.setTarget(new URI("Component2/pojoSvc3"));
        composite.add(wireDefn);
        compositeLoader.resolveWires(composite);
    }

    @SuppressWarnings("unchecked")
    public void testCompositeSvc2ComponentQualifiedInvalid() throws URISyntaxException {
        //undefined source and targets
        WireDefinition wireDefn = new WireDefinition();
        wireDefn.setSource(new URI("compositeService1"));
        wireDefn.setTarget(new URI("Component2/pojoSvc5"));
        composite.add(wireDefn);
        try {
            compositeLoader.resolveWires(composite);
            fail();
        } catch (InvalidWireException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testCompositeSvc2ComponentUnQualifiedInvalid() throws URISyntaxException {
        //undefined source and targets
        WireDefinition wireDefn = new WireDefinition();
        wireDefn.setSource(new URI("compositeService1"));
        wireDefn.setTarget(new URI("Component2"));
        composite.add(wireDefn);
        try {
            compositeLoader.resolveWires(composite);
            fail();
        } catch (InvalidWireException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testComponent2CompositeReferenceValid() throws Exception {
        //undefined source and targets
        WireDefinition wireDefn = new WireDefinition();
        wireDefn.setSource(new URI("Component1"));
        wireDefn.setTarget(new URI("compositeReference"));
        composite.add(wireDefn);
        compositeLoader.resolveWires(composite);
    }

    @SuppressWarnings("unchecked")
    public void testComponent2CompositeReferenceQualifiedValid() throws Exception {
        //undefined source and targets
        WireDefinition wireDefn = new WireDefinition();
        wireDefn.setSource(new URI("Component2/pojoRef3"));
        wireDefn.setTarget(new URI("compositeReference"));
        composite.add(wireDefn);
        compositeLoader.resolveWires(composite);
    }

    @SuppressWarnings("unchecked")
    public void testComponent2CompositeReferenceUnQualifiedInvalid() throws URISyntaxException {
        //undefined source and targets
        WireDefinition wireDefn = new WireDefinition();
        wireDefn.setSource(new URI("Component2"));
        wireDefn.setTarget(new URI("compositeReference"));
        composite.add(wireDefn);

        try {
            compositeLoader.resolveWires(composite);
            fail();
        } catch (InvalidWireException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testComponent2ComponentQualifedValid() throws Exception {
        //undefined source and targets
        WireDefinition wireDefn = new WireDefinition();
        wireDefn.setSource(new URI("Component1"));
        wireDefn.setTarget(new URI("Component2/pojoSvc3"));
        composite.add(wireDefn);
        compositeLoader.resolveWires(composite);
    }

    @SuppressWarnings("unchecked")
    public void testComponent2ComponentUnQualifedInvalid() throws URISyntaxException {
        //undefined source and targets
        WireDefinition wireDefn = new WireDefinition();
        wireDefn.setSource(new URI("Component1"));
        wireDefn.setTarget(new URI("Component2"));
        composite.add(wireDefn);
        try {
            compositeLoader.resolveWires(composite);
            fail();
        } catch (InvalidWireException e) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testInvalidWireDefinitions() throws URISyntaxException {
        //undefined source and targets
        WireDefinition wireDefn = new WireDefinition();
        wireDefn.setSource(new URI("undefinedSource"));
        wireDefn.setTarget(new URI("compositeReference"));
        composite.add(wireDefn);

        try {
            compositeLoader.resolveWires(composite);
            fail();
        } catch (InvalidWireException e) {
            // expected
        }

        try {
            wireDefn.setSource(new URI("compositeService1"));
            wireDefn.setTarget(new URI("undefinedTarget"));
            composite.add(wireDefn);
            compositeLoader.resolveWires(composite);
            fail();
        } catch (InvalidWireException e) {
            // expected
        }
    }
}
