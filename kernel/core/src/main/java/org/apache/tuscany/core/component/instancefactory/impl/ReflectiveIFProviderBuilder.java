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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.EagerInit;

import org.apache.tuscany.core.component.ReflectiveInstanceFactoryProvider;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderException;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSiteMapping;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSource;
import org.apache.tuscany.core.model.physical.instancefactory.MemberSite;
import org.apache.tuscany.core.model.physical.instancefactory.ReflectiveIFProviderDefinition;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * IF provider builder for reflective IF provider.
 *
 * @version $Date$ $Revision$
 */
@EagerInit
public class ReflectiveIFProviderBuilder<T> extends
    AbstractIFProviderBuilder<ReflectiveInstanceFactoryProvider<T>, ReflectiveIFProviderDefinition> {
    
    @Override
    protected Class<ReflectiveIFProviderDefinition> getIfpdClass() {
        return ReflectiveIFProviderDefinition.class;
    }

    @SuppressWarnings("unchecked")
    public ReflectiveInstanceFactoryProvider<T> build(ReflectiveIFProviderDefinition ifpd, ClassLoader cl)
        throws IFProviderBuilderException {

        try {

            Class implClass = cl.loadClass(ifpd.getImplementationClass());

            Constructor ctr = getConstructor(ifpd, cl, implClass);

            Method initMethod = getCallBackMethod(implClass, ifpd.getInitMethod());

            Method destroyMethod = getCallBackMethod(implClass, ifpd.getDestroyMethod());

            List<InjectionSource> ctrInjectSites = ifpd.getCdiSources();

            Map<InjectionSource, Member> injectionSites = getInjectionSites(ifpd, implClass);
            ReflectiveInstanceFactoryProvider<T> rifp =  new ReflectiveInstanceFactoryProvider<T>(ctr,
                ctrInjectSites,
                injectionSites,
                initMethod,
                destroyMethod);
            
            Map<InjectionSource, String> propertyValues = ifpd.getPropertyValues();
            for(InjectionSource source : propertyValues.keySet()) {
                Class<?> type = rifp.getMemberType(source);
                String propertyValue = propertyValues.get(source);
                ObjectFactory<?> factory = ObjectFactoryUtil.create(propertyValue, type);
                rifp.setObjectFactory(source, factory);
            }
            return rifp;

        } catch (ClassNotFoundException ex) {
            throw new IFProviderBuilderException(ex);
        } catch (NoSuchMethodException ex) {
            throw new IFProviderBuilderException(ex);
        } catch (NoSuchFieldException ex) {
            throw new IFProviderBuilderException(ex);
        } catch (IntrospectionException ex) {
            throw new IFProviderBuilderException(ex);
        }
    }

    /*
     * Get injection sites.
     */
    private Map<InjectionSource, Member> getInjectionSites(ReflectiveIFProviderDefinition ifpd, Class implClass)
        throws NoSuchFieldException, IntrospectionException, IFProviderBuilderException {

        Map<InjectionSource, Member> injectionSites = new HashMap<InjectionSource, Member>();
        for (InjectionSiteMapping injectionSite : ifpd.getInjectionSites()) {

            InjectionSource source = injectionSite.getSource();
            MemberSite memberSite = injectionSite.getSite();
            ElementType elementType = memberSite.getElementType();
            String name = memberSite.getName();

            Member member = null;
            if (memberSite.getElementType() == ElementType.FIELD) {
                member = implClass.getDeclaredField(name);
            } else if (elementType == ElementType.METHOD) {
                for (PropertyDescriptor pd : Introspector.getBeanInfo(implClass).getPropertyDescriptors()) {
                    if (name.equals(pd.getName())) {
                        member = pd.getWriteMethod();
                    }
                }
            }
            if (member == null) {
                throw new IFProviderBuilderException("Unknown injection site " + name);
            }
            injectionSites.put(source, member);
        }
        return injectionSites;
    }

    private Method getCallBackMethod(Class<?> implClass, String name) throws NoSuchMethodException {
        return name == null ? null : implClass.getMethod(name);
    }

    /*
     * Gets the matching constructor.
     */
    private Constructor getConstructor(ReflectiveIFProviderDefinition ifpd, ClassLoader cl, Class implClass)
        throws ClassNotFoundException, NoSuchMethodException {
        List<String> argNames = ifpd.getConstructorArguments();
        Class[] ctrArgs = new Class[argNames.size()];
        for (int i = 0; i < ctrArgs.length; i++) {
            ctrArgs[i] = cl.loadClass(argNames.get(i));
        }
        return implClass.getDeclaredConstructor(ctrArgs);
    }

}
