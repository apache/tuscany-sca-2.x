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
package org.apache.tuscany.spi.extension;

import javax.xml.namespace.QName;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.builder.interceptor.InterceptorBuilder;
import org.apache.tuscany.spi.builder.interceptor.InterceptorBuilderRegistry;

/**
 * Abstract class interceptor builders may extend to handle registration with the interceptor builder registry
 *
 * @version $Rev$ $Date$
 */
@EagerInit
public abstract class InterceptorBuilderExtension implements InterceptorBuilder {
    protected InterceptorBuilderRegistry registry;

    @Reference(required = true)
    public void setRegistry(InterceptorBuilderRegistry registry) {
        this.registry = registry;
    }

    @Init
    public void init() {
        registry.register(getName(), this);
    }

    protected abstract QName getName();
}
