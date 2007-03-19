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
package org.apache.tuscany.core.generator;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.generator.BindingGenerator;
import org.apache.tuscany.spi.generator.ComponentGenerator;
import org.apache.tuscany.spi.generator.GenerationException;
import org.apache.tuscany.spi.generator.GeneratorContext;
import org.apache.tuscany.spi.generator.GeneratorRegistry;
import org.apache.tuscany.spi.generator.InterceptorGenerator;
import org.apache.tuscany.spi.generator.ResourceGenerator;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.IntentDefinition;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ResourceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalInterceptorDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;

import org.apache.tuscany.core.model.NonBlockingIntentDefinition;

/**
 * @version $Rev$ $Date$
 */
public class GeneratorRegistryImpl implements GeneratorRegistry {

    private Map<Class<?>,
        ComponentGenerator<? extends ComponentDefinition<? extends Implementation>>> componentGenerators;
    private Map<Class<?>, BindingGenerator> bindingGenerators;
    private Map<Class<?>, InterceptorGenerator<? extends IntentDefinition>> interceptorGenerators;
    private Map<Class<?>, ResourceGenerator> resourceGenerators;


    public GeneratorRegistryImpl() {
        componentGenerators =
            new ConcurrentHashMap<Class<?>,
                ComponentGenerator<? extends ComponentDefinition<? extends Implementation>>>();
        bindingGenerators = new ConcurrentHashMap<Class<?>, BindingGenerator>();
        resourceGenerators = new ConcurrentHashMap<Class<?>, ResourceGenerator>();
        interceptorGenerators = new ConcurrentHashMap<Class<?>, InterceptorGenerator<? extends IntentDefinition>>();
    }

    public void register(Class<?> clazz, BindingGenerator generator) {
        bindingGenerators.put(clazz, generator);
    }

    public <T extends IntentDefinition> void register(Class<T> clazz, InterceptorGenerator<T> generator) {
        interceptorGenerators.put(clazz, generator);
    }

    public void register(Class<?> clazz, ResourceGenerator generator) {
        resourceGenerators.put(clazz, generator);
    }

    public <T extends Implementation<?>> void register(Class<T> clazz,
                                                       ComponentGenerator<ComponentDefinition<T>> generator) {
        componentGenerators.put(clazz, generator);
    }

    @SuppressWarnings({"unchecked"})
    public <C extends ComponentDefinition<? extends Implementation>> void generate(C definition,
                                                                                   GeneratorContext context)
        throws GenerationException {
        Class<?> type = definition.getImplementation().getClass();
        ComponentGenerator<C> generator = (ComponentGenerator<C>) componentGenerators.get(type);
        if (generator == null) {
            throw new GeneratorNotFoundException(type);
        }
        generator.generate(definition, context);
    }

    @SuppressWarnings({"unchecked"})
    public <C extends ComponentDefinition<? extends Implementation>>
    void generateWire(ServiceContract<?> contract,
                      BindingDefinition bindingDefinition,
                      ServiceDefinition serviceDefinition,
                      C componentDefinition,
                      GeneratorContext context) throws GenerationException {

        PhysicalWireDefinition wireDefinition = createWireDefinition(contract, context);
        Class<?> type = componentDefinition.getClass();
        ComponentGenerator<C> targetGenerator = (ComponentGenerator<C>) componentGenerators.get(type);
        if (targetGenerator == null) {
            throw new GeneratorNotFoundException(type);
        }
        PhysicalWireTargetDefinition targetDefinition =
            targetGenerator.generateWireTarget(componentDefinition, serviceDefinition, context);
        wireDefinition.setTarget(targetDefinition);
        type = bindingDefinition.getClass();
        BindingGenerator sourceGenerator = bindingGenerators.get(type);
        if (sourceGenerator == null) {
            throw new GeneratorNotFoundException(type);
        }
        PhysicalWireSourceDefinition sourceDefinition = sourceGenerator.generateWireSource(bindingDefinition, context);
        wireDefinition.setSource(sourceDefinition);
        context.getPhysicalChangeSet().addWireDefinition(wireDefinition);

    }

    @SuppressWarnings({"unchecked"})
    public <C extends ComponentDefinition<? extends Implementation>>
    void generateWire(C componentDefinition,
                      ReferenceDefinition referenceDefinition,
                      BindingDefinition bindingDefinition,
                      GeneratorContext context) throws GenerationException {

        ServiceContract<?> contract = referenceDefinition.getServiceContract();
        PhysicalWireDefinition wireDefinition = createWireDefinition(contract, context);
        Class<?> type = bindingDefinition.getClass();
        BindingGenerator targetGenerator = bindingGenerators.get(type);
        if (targetGenerator == null) {
            throw new GeneratorNotFoundException(type);
        }
        PhysicalWireTargetDefinition targetDefinition = targetGenerator.generateWireTarget(bindingDefinition, context);
        wireDefinition.setTarget(targetDefinition);

        type = componentDefinition.getClass();
        ComponentGenerator<C> sourceGenerator = (ComponentGenerator<C>) componentGenerators.get(type);
        if (sourceGenerator == null) {
            throw new GeneratorNotFoundException(type);
        }
        PhysicalWireSourceDefinition sourceDefinition =
            sourceGenerator.generateWireSource(componentDefinition, referenceDefinition, context);
        wireDefinition.setSource(sourceDefinition);

        context.getPhysicalChangeSet().addWireDefinition(wireDefinition);
    }

    @SuppressWarnings({"unchecked"})
    public <S extends ComponentDefinition<? extends Implementation>,
        T extends ComponentDefinition<? extends Implementation>>
    void generateWire(S source,
                      ReferenceDefinition referenceDefinition,
                      ServiceDefinition serviceDefinition,
                      T target,
                      GeneratorContext context) throws GenerationException {
        ServiceContract<?> contract = referenceDefinition.getServiceContract();
        PhysicalWireDefinition wireDefinition = createWireDefinition(contract, context);
        Class<?> type = target.getClass();
        ComponentGenerator<S> targetGenerator = (ComponentGenerator<S>) componentGenerators.get(type);
        if (targetGenerator == null) {
            throw new GeneratorNotFoundException(type);
        }
        PhysicalWireTargetDefinition targetDefinition =
            targetGenerator.generateWireTarget(source, serviceDefinition, context);
        wireDefinition.setTarget(targetDefinition);

        type = source.getClass();
        ComponentGenerator<T> sourceGenerator = (ComponentGenerator<T>) componentGenerators.get(type);
        if (sourceGenerator == null) {
            throw new GeneratorNotFoundException(type);
        }
        PhysicalWireSourceDefinition sourceDefinition =
            targetGenerator.generateWireSource(source, referenceDefinition, context);
        wireDefinition.setSource(sourceDefinition);
        context.getPhysicalChangeSet().addWireDefinition(wireDefinition);
    }

    public URI generate(ResourceDefinition definition, GeneratorContext context) throws GenerationException {
        Class<?> type = definition.getClass();
        ResourceGenerator generator = resourceGenerators.get(type);
        if (generator == null) {
            throw new GeneratorNotFoundException(type);
        }
        return generator.generate(definition, context);
    }


    @SuppressWarnings({"unchecked"})
    private PhysicalOperationDefinition mapOperation(Operation o) {
        PhysicalOperationDefinition operation = new PhysicalOperationDefinition();
        operation.setName(o.getName());
        operation.setConversationSequence(o.getConversationSequence());
        Type returnType = o.getOutputType().getPhysical();
        // TODO this needs to be fixed
        if (returnType instanceof Class) {
            operation.setReturnType(((Class) returnType).getName());
        } else if (returnType != null) {
            throw new AssertionError();
        }

        DataType<List<? extends DataType<?>>> params = o.getInputType();
        for (DataType<?> param : params.getLogical()) {
            Type paramType = param.getPhysical();
            // TODO this needs to be fixed
            if (paramType instanceof Class) {
                operation.addParameter(((Class) paramType).getName());
            } else if (paramType != null) {
                throw new AssertionError();
            }
        }
        return operation;

    }


    @SuppressWarnings({"unchecked"})
    private PhysicalWireDefinition createWireDefinition(ServiceContract<?> contract, GeneratorContext context)
        throws GenerationException {
        PhysicalWireDefinition wireDefinition = new PhysicalWireDefinition();
        for (Operation o : contract.getOperations().values()) {
            PhysicalOperationDefinition physicalOperation = mapOperation(o);
            wireDefinition.addOperation(physicalOperation);
            // this is egregious
            // hardcode intent until we get the intent infrastructure in place
            IntentDefinition intent = new NonBlockingIntentDefinition();
            Class<? extends IntentDefinition> type = NonBlockingIntentDefinition.class;
            InterceptorGenerator generator = interceptorGenerators.get(type);
            if (generator == null) {
                throw new GeneratorNotFoundException(type);
            }
            PhysicalInterceptorDefinition interceptorDefinition = generator.generate(intent, context);
            physicalOperation.addInterceptor(interceptorDefinition);
        }
        for (Operation o : contract.getCallbackOperations().values()) {
            PhysicalOperationDefinition physicalOperation = mapOperation(o);
            physicalOperation.setCallback(true);
            wireDefinition.addOperation(physicalOperation);
            // this is egregious
            // hardcode intent until we get the intent infrastructure in place
            IntentDefinition intent = new NonBlockingIntentDefinition();
            Class<? extends IntentDefinition> type = NonBlockingIntentDefinition.class;
            InterceptorGenerator generator = interceptorGenerators.get(type);
            if (generator == null) {
                throw new GeneratorNotFoundException(type);
            }
            PhysicalInterceptorDefinition interceptorDefinition = generator.generate(intent, context);
            physicalOperation.addInterceptor(interceptorDefinition);
        }
        return wireDefinition;
    }

}
