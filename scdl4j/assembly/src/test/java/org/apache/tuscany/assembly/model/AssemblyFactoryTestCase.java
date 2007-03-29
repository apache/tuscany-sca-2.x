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
package org.apache.tuscany.assembly.model;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.model.impl.DefaultAssemblyFactory;

/**
 * Test building of assembly model instances using the assembly factory.
 *
 *  @version $Rev$ $Date$
 */
public class AssemblyFactoryTestCase extends TestCase {
	
	AssemblyFactory factory;

	public void setUp() throws Exception {
		factory = new DefaultAssemblyFactory();
	}

	public void tearDown() throws Exception {
		factory = null;
	}

	public void testCreateComponent() {
		createComponent("AccountServiceComponent1");
	}

	public void testCreateComponentType() {
		createComponentType();
	}

	public void testCreateComposite() {
		createComposite();
	}

	public void testCreateConstrainingType() {
		createConstrainingType();
	}

	/**
	 * Create a composite
	 */
	Composite createComposite() {
		Composite c = factory.createComposite();
		
		Component c1 = createComponent("AccountServiceComponent1");
		c.getComponents().add(c1);
		Component c2 = createComponent("AccountServiceComponent2");
		c.getComponents().add(c2);
		
		Wire w = factory.createWire();
		w.setSource(c1.getReferences().get(0));
		w.setTarget(c2.getServices().get(0));
		c.getWires().add(w);
		
		CompositeService cs = factory.createCompositeService();
		cs.setName("AccountService");
		cs.setPromotedService(c1.getServices().get(0));
		cs.setInterface(new TestInterface(factory));
		c.getServices().add(cs);
		cs.getBindings().add(new TestBinding(factory));
		
		CompositeReference cr = factory.createCompositeReference();
		cr.setName("StockQuoteService");
		cr.getPromotedReferences().add(c2.getReferences().get(1));
		cr.setInterface(new TestInterface(factory));
		c.getReferences().add(cr);
		cr.getBindings().add(new TestBinding(factory));
		
		return c;
	}
	
	/**
	 * Create a new component
	 */
	Component createComponent(String name) {
		Component c = factory.createComponent();
		c.setName(name);
		
		ConstrainingType constraint = createConstrainingType();
		c.setConstrainingType(constraint);
		
		Implementation i = new TestImplementation(factory);
		c.setImplementation(i);
		
		ComponentProperty p = factory.createComponentProperty();
		p.setName("currency");
		p.setDefaultValue("USD");
		p.setMustSupply(true);
		p.setXSDType(new QName("", ""));
		p.setProperty(i.getProperties().get(0));
		c.getProperties().add(p);
		
		{
			ComponentReference r = factory.createComponentReference();
			r.setName("accountDataService");
			r.setMultiplicity(Multiplicity.ONE_ONE);
			r.setInterface(new TestInterface(factory));
			r.setReference(i.getReferences().get(0));
			c.getReferences().add(r);
			r.getBindings().add(new TestBinding(factory));
		}

		{
			ComponentReference r = factory.createComponentReference();
			r.setName("stockQuoteService");
			r.setMultiplicity(Multiplicity.ONE_ONE);
			r.setInterface(new TestInterface(factory));
			r.setReference(i.getReferences().get(1));
			c.getReferences().add(r);
			r.getBindings().add(new TestBinding(factory));
		}
		
		ComponentService s = factory.createComponentService();
		s.setName("AccountService");
		s.setInterface(new TestInterface(factory));
		s.setService(i.getServices().get(0));
		c.getServices().add(s);
		s.getBindings().add(new TestBinding(factory));
		
		return c;
	}
	
	/**
	 * Create a new component type
	 * @return
	 */
	ComponentType createComponentType() {
		ComponentType ctype = factory.createComponentType();
		
		Property p = factory.createProperty();
		p.setName("currency");
		p.setDefaultValue("USD");
		p.setMustSupply(true);
		p.setXSDType(new QName("", ""));
		ctype.getProperties().add(p);
		
		{
			Reference r = factory.createReference();
			r.setName("accountDataService");
			r.setInterface(new TestInterface(factory));
			r.setMultiplicity(Multiplicity.ONE_ONE);
			ctype.getReferences().add(r);
			r.getBindings().add(new TestBinding(factory));
		}

		{
			Reference r = factory.createReference();
			r.setName("stockQuoteService");
			r.setInterface(new TestInterface(factory));
			r.setMultiplicity(Multiplicity.ONE_ONE);
			ctype.getReferences().add(r);
			r.getBindings().add(new TestBinding(factory));
		}
		
		Service s = factory.createService();
		s.setName("AccountService");
		s.setInterface(new TestInterface(factory));
		ctype.getServices().add(s);
		s.getBindings().add(new TestBinding(factory));
		
		return ctype;
	}

	/**
	 * Create a new constraining type
	 * @return
	 */
	ConstrainingType createConstrainingType() {
		ConstrainingType ctype = factory.createConstrainingType();
		
		AbstractProperty p = factory.createAbstractProperty();
		p.setName("currency");
		p.setDefaultValue("USD");
		p.setMustSupply(true);
		p.setXSDType(new QName("", ""));
		ctype.getProperties().add(p);
		
		{
			AbstractReference r = factory.createAbstractReference();
			r.setName("accountDataService");
			r.setInterface(new TestInterface(factory));
			r.setMultiplicity(Multiplicity.ONE_ONE);
			ctype.getReferences().add(r);
		}

		{
			AbstractReference r = factory.createAbstractReference();
			r.setName("stockQuoteService");
			r.setInterface(new TestInterface(factory));
			r.setMultiplicity(Multiplicity.ONE_ONE);
			ctype.getReferences().add(r);
		}
		
		AbstractService s = factory.createAbstractService();
		s.setName("AccountService");
		s.setInterface(new TestInterface(factory));
		ctype.getServices().add(s);
		
		return ctype;
	}

}
