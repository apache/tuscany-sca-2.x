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
package org.apache.tuscany.core.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetInitializationException;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ReflectiveInstanceFactoryProviderTestCase extends TestCase {
    private Constructor<Foo> argConstructor;
    private List<InjectionSource> ctrNames;
    private Map<InjectionSource, Member> sites;
    private ObjectFactory intFactory;
    private ObjectFactory stringFactory;
    private ReflectiveInstanceFactoryProvider<Foo> provider;
    private Field intField;
    private Field stringField;
    private Method intSetter;
    private Method stringSetter;
    private InjectionSource intProperty = new InjectionSource(InjectionSource.ValueSourceType.PROPERTY, "int");
    private InjectionSource stringProperty = new InjectionSource(InjectionSource.ValueSourceType.PROPERTY, "string");

    public void testNoConstructorArgs() {
        ObjectFactory<?>[] args = provider.getConstructorArgs();
        assertEquals(0, args.length);
    }

    public void testConstructorArgs() {
        ctrNames.add(intProperty);
        ctrNames.add(stringProperty);
        provider = new ReflectiveInstanceFactoryProvider<Foo>(argConstructor,
                                                              ctrNames,
                                                              sites,
                                                              null,
                                                              null);
        provider.setObjectFactory(intProperty, intFactory);
        provider.setObjectFactory(stringProperty, stringFactory);
        ObjectFactory<?>[] args = provider.getConstructorArgs();
        assertEquals(2, args.length);
        assertSame(intFactory, args[0]);
        assertSame(stringFactory, args[1]);
    }

    public void testFieldInjectors() {
        sites.put(intProperty, intField);
        sites.put(stringProperty, stringField);
        Injector<Foo>[] injectors = provider.getInjectors();
        assertEquals(2, injectors.length);

        Foo foo = new Foo();
        for (Injector<Foo> injector : injectors) {
            assertTrue(injector instanceof FieldInjector);
            injector.inject(foo);
        }
        EasyMock.verify(intFactory, stringFactory);
        assertEquals(34, foo.intField);
        assertEquals("Hello", foo.stringField);
    }

    public void testMethodInjectors() {
        sites.put(intProperty, intSetter);
        sites.put(stringProperty, stringSetter);
        Injector<Foo>[] injectors = provider.getInjectors();
        assertEquals(2, injectors.length);

        Foo foo = new Foo();
        for (Injector<Foo> injector : injectors) {
            assertTrue(injector instanceof MethodInjector);
            injector.inject(foo);
        }
        EasyMock.verify(intFactory, stringFactory);
        assertEquals(34, foo.intField);
        assertEquals("Hello", foo.stringField);
    }

    public void testFactory() {
        sites.put(intProperty, intSetter);
        sites.put(stringProperty, stringField);
        InstanceFactory<Foo> instanceFactory = provider.createFactory();
        InstanceWrapper<Foo> instanceWrapper = instanceFactory.newInstance();
        try {
            instanceWrapper.start();
        } catch (TargetInitializationException e) {
            fail();
        }
        Foo foo = instanceWrapper.getInstance();
        EasyMock.verify(intFactory, stringFactory);
        assertEquals(34, foo.intField);
        assertEquals("Hello", foo.stringField);
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        Constructor<Foo> noArgConstructor = Foo.class.getConstructor();
        argConstructor = Foo.class.getConstructor(int.class, String.class);
        intField = Foo.class.getField("intField");
        stringField = Foo.class.getField("stringField");
        intSetter = Foo.class.getMethod("setIntField", int.class);
        stringSetter = Foo.class.getMethod("setStringField", String.class);
        ctrNames = new ArrayList<InjectionSource>();
        sites = new HashMap<InjectionSource, Member>();
        provider = new ReflectiveInstanceFactoryProvider<Foo>(noArgConstructor,
                                                              ctrNames,
                                                              sites,
                                                              null,
                                                              null);
        intFactory = EasyMock.createMock(ObjectFactory.class);
        stringFactory = EasyMock.createMock(ObjectFactory.class);
        EasyMock.expect(intFactory.getInstance()).andReturn(34);
        EasyMock.expect(stringFactory.getInstance()).andReturn("Hello");
        EasyMock.replay(intFactory, stringFactory);

        provider.setObjectFactory(intProperty, intFactory);
        provider.setObjectFactory(stringProperty, stringFactory);
    }

    public static class Foo {
        public int intField;
        public String stringField;

        public Foo() {
        }

        public Foo(int intField, String stringField) {
            this.intField = intField;
            this.stringField = stringField;
        }

        public void setIntField(int intField) {
            this.intField = intField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }
    }
}
