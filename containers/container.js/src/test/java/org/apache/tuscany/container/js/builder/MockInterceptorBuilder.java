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
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.wire.Interceptor;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;

/**
 * Adds an interceptor to a source or target proxy configuration
 * 
 * @version $Rev$ $Date$
 */
public class MockInterceptorBuilder implements ContextFactoryBuilder {

    private Interceptor interceptor;

    private boolean source;

    /**
     * Creates the builder
     * 
     * @param interceptor the interceptor ot add
     * @param source true if the interceptor should be added to the source side; false if the interceptor should be
     *        added to the target side
     */
    public MockInterceptorBuilder(Interceptor interceptor, boolean source) {
        this.interceptor = interceptor;
        this.source = source;
    }

    public void build(AssemblyObject modelObject) throws BuilderException {
        if (source) {
            if (!(modelObject instanceof ConfiguredReference)) {
                return;
            } else {
                ConfiguredReference cref = (ConfiguredReference) modelObject;
                // xcvProxyFactory pFactory = (WireFactory) cref.getProxyFactory();
                for (ConfiguredService configuredService : cref.getTargetConfiguredServices()) {
                    SourceWireFactory pFactory = (SourceWireFactory) configuredService.getProxyFactory();
                    for (SourceInvocationConfiguration config : pFactory.getConfiguration().getInvocationConfigurations().values()) {
                        config.addInterceptor(interceptor);
                    }
                }
            }
        } else {
            if (!(modelObject instanceof ConfiguredService)) {
                return;
            } else {
                ConfiguredService cservice = (ConfiguredService) modelObject;
                TargetWireFactory pFactory = (TargetWireFactory) cservice.getProxyFactory();
                for (TargetInvocationConfiguration config : pFactory.getConfiguration().getInvocationConfigurations().values()) {
                    config.addInterceptor(interceptor);
                }
            }

        }
    }

}
