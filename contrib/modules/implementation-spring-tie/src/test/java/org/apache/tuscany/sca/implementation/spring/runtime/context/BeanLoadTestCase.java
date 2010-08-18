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

package org.apache.tuscany.sca.implementation.spring.runtime.context;

import java.io.File;

import org.apache.tuscany.sca.implementation.spring.context.tie.SCAGenericApplicationContext;
import org.apache.tuscany.sca.implementation.spring.elements.tie.SpringBeanElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.UrlResource;

/**
 * 
 */
public class BeanLoadTestCase {

    private SCAGenericApplicationContext context;
    private XmlBeanDefinitionReader reader;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        context = new SCAGenericApplicationContext(null, getClass().getClassLoader());
        reader = new XmlBeanDefinitionReader(context);
    }

    @Test
    public void testLoad() throws Exception {
        File file =
            new File(
                     "/Users/rfeng/Perforce/perforce.internal.shutterfly.com_1666/rfeng_Raymond-Feng/SSP/giza/services/ecom/src/test/resources/com/shutterfly/services/ecom/ep.xml");
        UrlResource resource = new UrlResource(file.toURI().toURL());
        reader.loadBeanDefinitions(resource);
        for (SpringBeanElement beanElement : context.getBeanElements()) {
            System.out.println(beanElement);
        }

        System.out.println(context.getPropertyElements());
        System.out.println(context.getReferenceElements());
        System.out.println(context.getServiceElements());

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

}
