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
package org.apache.tuscany.container.spring.config;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;

/**
 * Overrides the default top-level Spring parser to use
 * {@link org.apache.tuscany.container.spring.config.SCANamespaceHandlerResolver}
 * for resolving namespace handlers
 *
 * @version $$Rev$$ $$Date$$
 */
public class SCARootDefinitionParser extends DefaultBeanDefinitionDocumentReader {

    private CompositeComponentType componentType;

    public SCARootDefinitionParser(CompositeComponentType componentType) {
        this.componentType = componentType;
    }

    protected NamespaceHandlerResolver createNamespaceHandlerResolver() {
        ClassLoader classLoader = getReaderContext().getReader().getBeanClassLoader();
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        return new SCANamespaceHandlerResolver(classLoader, componentType);
    }


}
