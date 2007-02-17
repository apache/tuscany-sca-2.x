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
package org.apache.tuscany.core.policy;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.policy.SourcePolicyBuilder;
import org.apache.tuscany.spi.policy.TargetPolicyBuilder;

/**
 * The default policy builder
 *
 * @version $Rev$ $Date$
 * @deprecated
 */
public class PolicyBuilderRegistryImpl implements PolicyBuilderRegistry {

    private final List<List<SourcePolicyBuilder>> sourceBuilders;
    private final List<List<TargetPolicyBuilder>> targetBuilders;

    public PolicyBuilderRegistryImpl() {
        sourceBuilders = new ArrayList<List<SourcePolicyBuilder>>();
        targetBuilders = new ArrayList<List<TargetPolicyBuilder>>();
        for (int i = 0; i <= FINAL; i++) {
            sourceBuilders.add(new ArrayList<SourcePolicyBuilder>());
            targetBuilders.add(new ArrayList<TargetPolicyBuilder>());
        }
    }

    public void registerTargetBuilder(int phase, TargetPolicyBuilder builder) {
        assert INITIAL <= phase && phase <= FINAL : "Illegal phase";
        targetBuilders.get(phase).add(builder);
    }

    public void registerSourceBuilder(int phase, SourcePolicyBuilder builder) {
        assert INITIAL <= phase && phase <= FINAL : "Illegal phase";
        sourceBuilders.get(phase).add(builder);
    }


}
