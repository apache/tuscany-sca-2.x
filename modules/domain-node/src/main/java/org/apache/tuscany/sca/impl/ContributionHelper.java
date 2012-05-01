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
package org.apache.tuscany.sca.impl;

import java.net.URLClassLoader;

import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.java.impl.ClassLoaderModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.jaxb.JAXBContextHelper;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;

import sun.misc.ClassLoaderUtil;

public class ContributionHelper {

	public static void close(Contribution contribution, ExtensionPointRegistry extensionPointRegistry) {
        ClassLoader contributionClassloader = contribution.getClassLoader();  

        if (contributionClassloader == null && contribution.getModelResolver() instanceof ExtensibleModelResolver) {
            ModelResolver o = ((ExtensibleModelResolver)contribution.getModelResolver()).getModelResolverInstance(ClassReference.class);
            if (o instanceof ClassLoader) {
            	contributionClassloader = (ClassLoader)o;        
            	contribution.setClassLoader(contributionClassloader);
            }
        }
        
        UtilityExtensionPoint utilityExtensionPoint = extensionPointRegistry.getExtensionPoint(UtilityExtensionPoint.class);
        FactoryExtensionPoint factoryExtensionPoint = extensionPointRegistry.getExtensionPoint(FactoryExtensionPoint.class);

        JAXBContextHelper jaxbContextHelper = utilityExtensionPoint.getUtility(JAXBContextHelper.class);
        jaxbContextHelper.removeJAXBContextForContribution(contributionClassloader);
        
        JavaInterfaceFactory javaInterfaceFactory = factoryExtensionPoint.getFactory(JavaInterfaceFactory.class);
        javaInterfaceFactory.removeInterfacesForContribution(contributionClassloader);
        
        ProxyFactoryExtensionPoint proxyFactoryExtensionPoint = extensionPointRegistry.getExtensionPoint(ProxyFactoryExtensionPoint.class);
        ProxyFactory interfaceProxyFactory = proxyFactoryExtensionPoint.getInterfaceProxyFactory();
        interfaceProxyFactory.removeProxiesForContribution(contributionClassloader);

        DOMHelper.getInstance(extensionPointRegistry).stop();
        java.beans.Introspector.flushCaches();

        if (contributionClassloader instanceof URLClassLoader) {
            ClassLoaderUtil.releaseLoader((URLClassLoader)contributionClassloader);
        }

        if (contributionClassloader instanceof ClassLoaderModelResolver) {
        	ClassLoaderModelResolver clmr = (ClassLoaderModelResolver) contributionClassloader;
        	clmr.clear();
        }

        contribution.setClassLoader(null);
	}
	
}
