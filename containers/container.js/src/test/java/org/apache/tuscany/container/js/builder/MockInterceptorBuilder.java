/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.container.js.builder;


import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.SourcePolicyBuilder;
import org.apache.tuscany.core.builder.TargetPolicyBuilder;
import org.apache.tuscany.core.wire.Interceptor;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;

import java.util.List;

/**
 * Adds an interceptor to a source or target proxy configuration
 *
 * @version $Rev$ $Date$
 */
public class MockInterceptorBuilder implements SourcePolicyBuilder, TargetPolicyBuilder {

    private Interceptor interceptor;

    /**
     * Creates the builder
     *
     * @param interceptor the interceptor to add
     */
    public MockInterceptorBuilder(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public void build(ConfiguredReference reference, List<WireSourceConfiguration> configurations) throws BuilderException {
        for (WireSourceConfiguration wireSourceConfiguration : configurations) {
            for (SourceInvocationConfiguration configuration : wireSourceConfiguration.getInvocationConfigurations().values()) {
                configuration.addInterceptor(interceptor);
            }
        }
    }

    public void build(ConfiguredService service, WireTargetConfiguration configuration) throws BuilderException {
        for (TargetInvocationConfiguration config : configuration.getInvocationConfigurations().values()) {
            config.addInterceptor(interceptor);
        }
    }
}
