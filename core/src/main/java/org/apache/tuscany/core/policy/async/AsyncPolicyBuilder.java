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
package org.apache.tuscany.core.policy.async;

import org.apache.tuscany.spi.services.work.WorkScheduler;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.OneWay;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import static org.apache.tuscany.spi.policy.PolicyBuilderRegistry.INITIAL;
import org.apache.tuscany.spi.policy.TargetPolicyBuilder;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;

import org.apache.tuscany.core.monitor.NullMonitorFactory;

/**
 * A policy builder for handling the {@link OneWay} annotation
 *
 * @version $Rev$ $Date$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class AsyncPolicyBuilder implements TargetPolicyBuilder {

    private PolicyBuilderRegistry builderRegistry;
    private WorkScheduler workScheduler;
    private AsyncMonitor monitor;

    public AsyncPolicyBuilder() {
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.registerTargetBuilder(INITIAL, this);
        if (monitor == null) {
            monitor = new NullMonitorFactory().getMonitor(AsyncMonitor.class);
        }
    }

    @Autowire
    public void setBuilderRegistry(PolicyBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    @org.apache.tuscany.api.annotation.Monitor
    public void setMonitor(AsyncMonitor monitor) {
        this.monitor = monitor;
    }

    @Autowire
    public void setWorkScheduler(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    public void build(ServiceDefinition serviceDefinition, InboundWire<?> wire) throws BuilderException {
        for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
            // TODO fix this - it should be represented by the model and not through an annotation
            if (chain.getMethod().getAnnotation(OneWay.class) != null) {
                chain.addInterceptor(new AsyncInterceptor(workScheduler, monitor));
            }
        }
    }
}
