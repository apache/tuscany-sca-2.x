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
package org.apache.tuscany.container.js.mock;

import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.container.js.assembly.mock.HelloWorldService;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * Generates mock modules
 * 
 * @version $Rev$ $Date$
 */
public class MockModuleFactory {

    private static AssemblyFactory factory = new AssemblyFactoryImpl();

    private static AssemblyModelContext assemblyContext = new AssemblyModelContextImpl(new AssemblyFactoryImpl(),
            new SCDLAssemblyModelLoaderImpl(null), new ResourceLoaderImpl(Thread.currentThread().getContextClassLoader()));

    private MockModuleFactory() {
    }

    public static Module createModule() throws Exception {
        Component sourceComponent = MockAssemblyFactory.createComponent("source",
                "org/apache/tuscany/container/js/assembly/mock/HelloWorldImpl.js", HelloWorldService.class, Scope.MODULE);
        Component targetComponent = MockAssemblyFactory.createComponent("target",
                "org/apache/tuscany/container/js/assembly/mock/HelloWorldImpl.js", HelloWorldService.class, Scope.MODULE);

        Service targetService = factory.createService();
        JavaServiceContract targetContract = factory.createJavaServiceContract();
        targetContract.setInterface(HelloWorldService.class);
        targetService.setServiceContract(targetContract);
        targetService.setName("GenericComponent");
        ConfiguredService cTargetService = factory.createConfiguredService();
        cTargetService.setService(targetService);
        cTargetService.initialize(assemblyContext);
        targetComponent.getConfiguredServices().add(cTargetService);
        targetComponent.initialize(assemblyContext);

        Reference ref = factory.createReference();
        ref.setName("setGenericComponent");
        JavaServiceContract inter = factory.createJavaServiceContract();
        inter.setInterface(HelloWorldService.class);
        ref.setServiceContract(inter);
        sourceComponent.getComponentImplementation().getComponentType().getReferences().add(ref);
        ConfiguredReference cref = factory.createConfiguredReference();
        cref.setName(ref.getName());
        cref.getTargetConfiguredServices().add(cTargetService);
        cref.initialize(assemblyContext);
        sourceComponent.getConfiguredReferences().add(cref);
        sourceComponent.initialize(assemblyContext);

        Module module = factory.createModule();
        module.setName("test.module");
        module.getComponents().add(sourceComponent);
        module.getComponents().add(targetComponent);
        module.initialize(assemblyContext);
        return module;
    }

    // public static Module createModule() throws Exception {
    // Component sourceComponent =
    // MockAssemblyFactory.createComponent("source","org/apache/tuscany/container/js/assembly/mock/HelloWorldImpl.js",HelloWorldService.class.getCanonicalName(),ScopeEnum.MODULE_LITERAL);
    // Component targetComponent =
    // MockAssemblyFactory.createComponent("target","org/apache/tuscany/container/js/assembly/mock/HelloWorldImpl.js",HelloWorldService.class.getCanonicalName(),ScopeEnum.MODULE_LITERAL);
    // PojoReference ref = new PojoReference();
    // PojoConfiguredReference cref = new PojoConfiguredReference();
    // ref.setName("helloWorld");
    // PojoInterface inter = new PojoJavaInterface();
    // PojoInterfaceType type = new PojoInterfaceType();
    // type.setInstanceClass(HelloWorldService.class);
    // PojoOperationType oType = new PojoOperationType();
    // oType.setName("hello");
    // SDOType inputType = new SDOType("String","",String.class,null);
    // oType.setInputType(inputType);
    // type.addOperationType(oType);
    // inter.setInterfaceType(type);
    // ref.setInterfaceContract(inter);
    // cref.setReference(ref);
    // cref.setPart(targetComponent);
    // PojoPort port = new PojoPort();
    // port.setName("HelloWorldService");
    // cref.setPort(port);
    // sourceComponent.getConfiguredReferences().add(cref);
    // PojoModule module = new PojoModule();
    // module.setName("test.module");
    // module.addComponent(sourceComponent);
    // module.addComponent(targetComponent);
    // return module;
    // }

}
