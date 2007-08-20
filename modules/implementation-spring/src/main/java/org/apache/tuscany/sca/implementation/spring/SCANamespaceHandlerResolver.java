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
package org.apache.tuscany.sca.implementation.spring;

import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.NamespaceHandler;

/**
 * Overrides the default Spring namespace resolver to automatically register
 * {@link ScaNamespaceHandler} instead of requiring a value to be supplied in a
 * Spring configuration
 * 
 * @version $$Rev: 511195 $$ $$Date: 2007-02-24 02:29:46 +0000 (Sat, 24 Feb 2007) $$
 */
public class SCANamespaceHandlerResolver extends DefaultNamespaceHandlerResolver {
    private static final String SCA_NAMESPACE = "http://www.springframework.org/schema/sca";

    private ScaNamespaceHandler handler;

    public SCANamespaceHandlerResolver(ClassLoader classLoader) {
        super(classLoader);
        handler = new ScaNamespaceHandler(/*componentType*/);
    }

    public SCANamespaceHandlerResolver(String handlerMappingsLocation, ClassLoader classLoader) {
        super(classLoader, handlerMappingsLocation);
        handler = new ScaNamespaceHandler(/*componentType*/);
    }

    @Override
    public NamespaceHandler resolve(String namespaceUri) {
        if (SCA_NAMESPACE.equals(namespaceUri)) {
            return handler;
        }
        return super.resolve(namespaceUri);
    }
}
