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
package org.apache.tuscany.core.extension;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.service.WireFactoryService;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Service;
import org.osoa.sca.annotations.Init;

/**
 * A base class for a {@link ContextFactoryBuilder} that creates {@link org.apache.tuscany.core.context.ExternalServiceContext}s
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class ExternalServiceBuilderSupport<T extends Binding> implements ContextFactoryBuilder {

    private ContextFactoryBuilderRegistry builderRegistry;
    private WireFactoryService wireService;
    protected Class bindingClass;

    public ExternalServiceBuilderSupport() {
        // reflect the generic type of the subclass
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            bindingClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            throw new AssertionError("Subclasses of " + ContextFactoryBuilderSupport.class.getName() + " must be genericized");
        }
    }

    public ExternalServiceBuilderSupport(WireFactoryService wireService) {
        this();
        this.wireService = wireService;
    }

    @Init(eager = true)
    public void init() throws Exception {
        builderRegistry.register(this);
    }

    @Autowire
    public void setBuilderRegistry(ContextFactoryBuilderRegistry registry) {
        builderRegistry = registry;
    }

    @Autowire
    public void setWireService(WireFactoryService wireService) {
        this.wireService = wireService;
    }

    public void build(AssemblyObject object) throws BuilderException {
        if (!(object instanceof ExternalService)) {
            return;
        }
        ExternalService externalService = (ExternalService) object;
        if (externalService.getBindings().size() < 1) {
            //   || !(handlesBindingType(externalService.getBindings().get(0)))) {
            return;
        }
        if (!bindingClass.isAssignableFrom(externalService.getBindings().get(0).getClass())) {
            return;
        }

        ExternalServiceContextFactory contextFactory
                = createExternalServiceContextFactory(externalService);

        ConfiguredService configuredService = externalService.getConfiguredService();
        Service service = configuredService.getPort();
        TargetWireFactory wireFactory = wireService.createTargetFactory(configuredService);
        contextFactory.addTargetWireFactory(service.getName(), wireFactory);
        externalService.setContextFactory(contextFactory);
    }

    /**
     * Returns true if an extending implementation can process the given binding element
     */
    //protected abstract boolean handlesBindingType(Binding binding);

    /**
     * Callback to create the specific <code>ContextFactory</code> type associated with the extending
     * implementation
     *
     * @param externalService the external service being processed
     */
    protected abstract ExternalServiceContextFactory createExternalServiceContextFactory(ExternalService externalService);

}
