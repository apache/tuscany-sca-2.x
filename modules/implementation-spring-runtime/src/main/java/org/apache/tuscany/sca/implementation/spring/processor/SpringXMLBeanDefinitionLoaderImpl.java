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

package org.apache.tuscany.sca.implementation.spring.processor;

import java.net.URL;
import java.util.List;

import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.implementation.spring.SpringBeanElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAPropertyElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAReferenceElement;
import org.apache.tuscany.sca.implementation.spring.SpringSCAServiceElement;
import org.apache.tuscany.sca.implementation.spring.context.SCAGenericApplicationContext;
import org.apache.tuscany.sca.implementation.spring.xml.SpringXMLBeanDefinitionLoader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.UrlResource;

/**
 * A tie that allows Tuscany to call Spring library to load the application context for the purpose of introspection
 */
public class SpringXMLBeanDefinitionLoaderImpl implements SpringXMLBeanDefinitionLoader {

    private static SCAGenericApplicationContext createApplicationContext(Object scaParentContext,
                                                                         ClassLoader classLoader,
                                                                         List<URL> resources) {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        SCAGenericApplicationContext appCtx =
            new SCAGenericApplicationContext((ApplicationContext)scaParentContext, classLoader);
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(appCtx);

        // REVIEW: [rfeng] How do we control the schema validation 
        xmlReader.setValidating(false);

        for (URL resource : resources) {
            xmlReader.loadBeanDefinitions(new UrlResource(resource));
        }

        return appCtx;

    }

    @Override
    public Object load(List<URL> resources,
                       List<SpringSCAServiceElement> serviceElements,
                       List<SpringSCAReferenceElement> referenceElements,
                       List<SpringSCAPropertyElement> propertyElements,
                       List<SpringBeanElement> beanElements,
                       ProcessorContext context) {
        // FIXME: [rfeng] We should try to avoid parsing the Spring xml twice for the deployment and runtime
        SCAGenericApplicationContext applicationContext = createApplicationContext(null, null, resources);
        serviceElements.addAll(applicationContext.getServiceElements());
        referenceElements.addAll(applicationContext.getReferenceElements());
        propertyElements.addAll(applicationContext.getPropertyElements());
        beanElements.addAll(applicationContext.getBeanElements());
        return applicationContext;
    }

}
