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
package org.apache.tuscany.core.component.instancefactory.impl;

import java.lang.annotation.ElementType;

import junit.framework.TestCase;

import org.apache.tuscany.core.component.ReflectiveInstanceFactoryProvider;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSiteMapping;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSource;
import org.apache.tuscany.core.model.physical.instancefactory.MemberSite;
import org.apache.tuscany.core.model.physical.instancefactory.ReflectiveIFProviderDefinition;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSource.ValueSourceType;

/**
 * 
 * @version $Date$ $Revision$
 *
 */
public class ReflectiveIFProviderBuilderTest extends TestCase {
    
    public void testBuild() throws Exception {
        
        ReflectiveIFProviderDefinition def = new ReflectiveIFProviderDefinition();
        
        def.setImplementationClass("org.apache.tuscany.core.component.instancefactory.impl.Foo");
        def.setDestroyMethod("destroy");
        def.setInitMethod("init");
        
        def.addConstructorArgument("java.lang.String");
        def.addConstructorArgument("java.lang.Long");
        
        InjectionSource cdiSource = new InjectionSource();
        cdiSource.setName("abc");
        cdiSource.setValueType(ValueSourceType.REFERENCE);
        def.addCdiSource(cdiSource);
        
        InjectionSiteMapping injectionSite = new InjectionSiteMapping();
        InjectionSource injectionSource = new InjectionSource();
        injectionSource.setName("xyz");
        injectionSource.setValueType(ValueSourceType.PROPERTY);
        MemberSite memberSite = new MemberSite();
        memberSite.setName("xyz");
        memberSite.setElementType(ElementType.FIELD);
        injectionSite.setSite(memberSite);
        injectionSite.setSource(injectionSource);        
        def.addInjectionSite(injectionSite);
        
        injectionSite = new InjectionSiteMapping();
        injectionSource = new InjectionSource();
        injectionSource.setName("abc");
        injectionSource.setValueType(ValueSourceType.CALLBACK);
        memberSite = new MemberSite();
        memberSite.setName("abc");
        memberSite.setElementType(ElementType.METHOD);
        injectionSite.setSite(memberSite);
        injectionSite.setSource(injectionSource);        
        def.addInjectionSite(injectionSite);
        
        ReflectiveIFProviderBuilder builder = new ReflectiveIFProviderBuilder();
        ClassLoader cl = getClass().getClassLoader();
        
        
        ReflectiveInstanceFactoryProvider provider = builder.build(def, cl);
        assertNotNull(provider);
        
        Class<?> clazz = provider.getMemberType(injectionSource);
        assertNotNull(clazz);
        assertEquals(Bar.class, clazz);
    }

}
