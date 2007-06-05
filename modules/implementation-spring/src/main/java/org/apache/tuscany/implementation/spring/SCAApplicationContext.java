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
package org.apache.tuscany.implementation.spring;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;

/**
 * An <code>ApplicationContext</code> specialization that registers namespace handlers for 
 * SCA elements - in particular the <service/>, <reference/> and <property/> elements which
 * are provided as SCA extensions to the Spring application context schema
 *
 */
public class SCAApplicationContext extends AbstractXmlApplicationContext {
    public static final String APP_CONTEXT_PROP = "org.springframework.sca.application.context";
    private Resource appXml;

    public SCAApplicationContext(ApplicationContext parent, Resource appXml) {
        super(parent);
        this.appXml = appXml;
        refresh();
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
        ClassLoader cl = getClassLoader();
        beanDefinitionReader.setNamespaceHandlerResolver(new SCANamespaceHandlerResolver(cl));
    }

    protected Resource[] getConfigResources() {
        return new Resource[]{appXml};
    }
}
