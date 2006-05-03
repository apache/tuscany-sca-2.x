/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.async.builder;

import java.util.List;

import javax.resource.spi.work.WorkManager;

import org.apache.tuscany.core.async.invocation.AsyncInterceptor;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.builder.SourcePolicyBuilder;
import org.apache.tuscany.core.builder.TargetPolicyBuilder;
import org.apache.tuscany.core.builder.system.PolicyBuilderRegistry;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.OneWay;

/**
 * Builds context factories for component implementations that map to {@link org.apache.tuscany.container.java.assembly.JavaImplementation}.
 * The logical model is then decorated with the runtime configuration.
 *
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 * @see org.apache.tuscany.core.builder.ContextFactory
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class AsyncPolicyBuilder implements SourcePolicyBuilder, TargetPolicyBuilder {

    private PolicyBuilderRegistry builderRegistry;
    private WorkManager workManager;
    private MessageFactory messageFactory;

    public AsyncPolicyBuilder() {
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.registerSourceBuilder(this);
        builderRegistry.registerTargetBuilder(this);
    }

    @Autowire
    public void setBuilderRegistry(PolicyBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }
    
    @Autowire
    public void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
    }
    
    @Autowire
    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public void build(ConfiguredReference arg0, List<WireSourceConfiguration> arg1) throws BuilderException {
    }
    
    public void build(ConfiguredService service, WireTargetConfiguration wireTargetConfiguration) throws BuilderException {
        for (TargetInvocationConfiguration configuration : wireTargetConfiguration.getInvocationConfigurations().values()) {
            if (configuration.getMethod().getAnnotation(OneWay.class)!=null) {
                configuration.addInterceptor(new AsyncInterceptor(workManager, messageFactory));
            }
        }
    }
}
