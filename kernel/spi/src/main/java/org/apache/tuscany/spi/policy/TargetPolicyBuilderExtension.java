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
package org.apache.tuscany.spi.policy;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * A runtime extension point for {@link org.apache.tuscany.spi.policy.SourcePolicyBuilder}s
 *
 * @version $Rev$ $Date$
 */
@EagerInit
public abstract class TargetPolicyBuilderExtension implements TargetPolicyBuilder {
    protected int phase = PolicyBuilderRegistry.EXTENSION;
    private PolicyBuilderRegistry registry;

    @Reference
    public void setRegistry(PolicyBuilderRegistry registry) {
        this.registry = registry;
    }

    @Property
    public void setPhase(int phase) {
        this.phase = phase;
    }

    @Init
    public void init() {
        registry.registerTargetBuilder(phase, this);
    }


}
