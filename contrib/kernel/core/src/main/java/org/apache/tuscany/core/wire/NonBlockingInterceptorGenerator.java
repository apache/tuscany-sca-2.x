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
package org.apache.tuscany.core.wire;

import javax.xml.namespace.QName;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.generator.GenerationException;
import org.apache.tuscany.spi.generator.GeneratorContext;
import org.apache.tuscany.spi.generator.GeneratorRegistry;
import org.apache.tuscany.spi.generator.InterceptorGenerator;
import org.apache.tuscany.spi.model.physical.PhysicalInterceptorDefinition;

import org.apache.tuscany.core.model.NonBlockingIntentDefinition;

/**
 * Generates an interceptor definition for non-blocking operations
 *
 * @version $Rev$ $Date$
 */
@EagerInit
public class NonBlockingInterceptorGenerator implements InterceptorGenerator<NonBlockingIntentDefinition> {
    public static final QName QNAME = new QName("http://tuscany.apache.org/xmlns/sca/system/2.0-alpha", "nonblocking");

    public NonBlockingInterceptorGenerator(@Reference GeneratorRegistry registry) {
        registry.register(NonBlockingIntentDefinition.class, this);
    }

    public PhysicalInterceptorDefinition generate(NonBlockingIntentDefinition definition,
                                                  GeneratorContext context) throws GenerationException {
        return new PhysicalInterceptorDefinition(QNAME);
    }
}
