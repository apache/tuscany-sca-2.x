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
package org.apache.tuscany.core.builder.interceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.interceptor.InterceptorBuilder;
import org.apache.tuscany.spi.builder.interceptor.InterceptorBuilderRegistry;
import org.apache.tuscany.spi.model.physical.PhysicalInterceptorDefinition;
import org.apache.tuscany.spi.wire.Interceptor;

/**
 * Default implementation of an InterceptorBuilderRegistry
 *
 * @version $Rev$ $Date$
 */
public class InterceptorBuilderRegistryImpl implements InterceptorBuilderRegistry {
    private Map<QName, InterceptorBuilder> builders = new ConcurrentHashMap<QName, InterceptorBuilder>();

    public void register(QName name, InterceptorBuilder builder) {
        builders.put(name, builder);
    }

    public void unregister(QName name) {
        builders.remove(name);
    }

    public Interceptor build(PhysicalInterceptorDefinition definition) throws BuilderException {
        QName name = definition.getBuilder();
        InterceptorBuilder builder = builders.get(name);
        if (builder == null) {
            throw new InterceptorBuilderNotFoundException(name);
        }
        return builder.build(definition);
    }
}
