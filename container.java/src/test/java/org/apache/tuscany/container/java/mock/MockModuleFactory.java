package org.apache.tuscany.container.java.mock;

import org.apache.tuscany.container.java.assembly.mock.HelloWorldImpl;
import org.apache.tuscany.container.java.assembly.mock.HelloWorldService;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.container.java.mock.components.HelloWorldClient;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponentImpl;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;

public class MockModuleFactory {

    private static AssemblyFactory factory = new AssemblyFactoryImpl();
    
    private static AssemblyModelContext assemblyContext = new AssemblyModelContextImpl(null, null,null); 

    private MockModuleFactory() {
    }
    
    public static Module createModule() throws Exception {
        Component sourceComponent = MockAssemblyFactory.createComponent("source", ModuleScopeComponentImpl.class,Scope.MODULE);
        Component targetComponent = MockAssemblyFactory.createComponent("target", ModuleScopeComponentImpl.class,Scope.MODULE);

        Service targetService = factory.createService();
        JavaServiceContract targetContract = factory.createJavaServiceContract();
        targetContract.setInterface(GenericComponent.class);
        targetService.setServiceContract(targetContract);
        targetService.setName("GenericComponent");
        ConfiguredService cTargetService = factory.createConfiguredService();
        cTargetService.setService(targetService);
        cTargetService.initialize(assemblyContext);
        targetComponent.getConfiguredServices().add(cTargetService);
        targetComponent.initialize(assemblyContext);
        
        Reference ref = factory.createReference();
        ConfiguredReference cref = factory.createConfiguredReference();
        ref.setName("setGenericComponent");
        JavaServiceContract inter = factory.createJavaServiceContract();
        inter.setInterface(GenericComponent.class);
        ref.setServiceContract(inter);
        cref.setReference(ref);
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

    
    public static Module createModuleWithExternalService() throws Exception {
        Component sourceComponent = MockAssemblyFactory.createComponent("source", HelloWorldClient.class,Scope.MODULE);
        ExternalService targetES = MockAssemblyFactory.createFooBindingExternalService("target",HelloWorldService.class);

        Service targetService = factory.createService();
        JavaServiceContract targetContract = factory.createJavaServiceContract();
        targetContract.setInterface(HelloWorldService.class);
        targetService.setServiceContract(targetContract);
        targetService.setName("HelloWorld");
        ConfiguredService cTargetService = factory.createConfiguredService();
        cTargetService.setService(targetService);
        targetES.setConfiguredService(cTargetService);
        targetES.initialize(assemblyContext);
        
        Reference ref = factory.createReference();
        ConfiguredReference cref = factory.createConfiguredReference();
        ref.setName("setHelloWorldService");
        JavaServiceContract inter = factory.createJavaServiceContract();
        inter.setInterface(HelloWorldService.class);
        ref.setServiceContract(inter);
        cref.setReference(ref);
        cref.getTargetConfiguredServices().add(cTargetService);
        cref.initialize(assemblyContext);
        sourceComponent.getConfiguredReferences().add(cref);
        sourceComponent.initialize(assemblyContext);

        Module module = factory.createModule();
        module.setName("test.module");
        module.getComponents().add(sourceComponent);
        module.getExternalServices().add(targetES);
        module.initialize(assemblyContext);
        return module;
    }

    
    public static Module createModuleWithEntryPoint() throws Exception {
        EntryPoint sourceEP = MockAssemblyFactory.createFooBindingEntryPoint("source", HelloWorldService.class);
        Component targetComponent = MockAssemblyFactory.createComponent("target",HelloWorldImpl.class,Scope.MODULE);

        Service targetService = factory.createService();
        JavaServiceContract targetContract = factory.createJavaServiceContract();
        targetContract.setInterface(HelloWorldService.class);
        targetService.setServiceContract(targetContract);
        targetService.setName("HelloWorldService");
        ConfiguredService cTargetService = factory.createConfiguredService();
        cTargetService.setService(targetService);
        targetComponent.getConfiguredServices().add(cTargetService);
        targetComponent.initialize(assemblyContext);
        
        Reference ref = factory.createReference();
        ConfiguredReference cref = factory.createConfiguredReference();
        ref.setName("setHelloWorldService");
        JavaServiceContract inter = factory.createJavaServiceContract();
        inter.setInterface(HelloWorldService.class);
        ref.setServiceContract(inter);
        cref.setReference(ref);
        cref.getTargetConfiguredServices().add(cTargetService);
        cref.initialize(assemblyContext);
        sourceEP.setConfiguredReference(cref);
        sourceEP.getConfiguredService().getService().setName("HelloWorldService");
        sourceEP.initialize(assemblyContext);
        

        Module module = factory.createModule();
        module.setName("test.module");
        module.getEntryPoints().add(sourceEP);
        module.getComponents().add(targetComponent);
        module.initialize(assemblyContext);
        return module;
    }

    
}

