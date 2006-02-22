package org.apache.tuscany.container.java.mock;

import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponentImpl;
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
import org.apache.tuscany.model.types.java.JavaServiceContract;

public class MockModuleFactory {

    private static AssemblyFactory factory = new AssemblyFactoryImpl();
    
    private static AssemblyModelContext assemblyContext = new AssemblyModelContextImpl(null,null); 

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

//    public static Module createModule() throws Exception {
//        Component sourceComponent = MockAssemblyFactory.createComponent("source", ModuleScopeComponentImpl.class,
//                Scope.MODULE);
//        Component targetComponent = MockAssemblyFactory.createComponent("target", ModuleScopeComponentImpl.class,
//                Scope.MODULE);
//        PojoReference ref = new PojoReference();
//        PojoConfiguredReference cref = new PojoConfiguredReference();
//        ref.setName("setGenericComponent");
//        PojoInterface inter = new PojoJavaInterface();
//        PojoInterfaceType type = new PojoInterfaceType();
//        type.setInstanceClass(GenericComponent.class);
//        PojoJavaOperationType oType = new PojoJavaOperationType();
//        oType.setName("getString");
//        oType.setJavaMethod((Method) JavaIntrospectionHelper.getBeanProperty(GenericComponent.class, "getString", null));
//        type.addOperationType(oType);
//        inter.setInterfaceType(type);
//        ref.setServiceContract(inter);
//        cref.setReference(ref);
//        cref.setPart(targetComponent);
//        PojoPort port = new PojoPort();
//        port.setName("GenericComponent");
//        cref.setPort(port);
//        sourceComponent.getConfiguredReferences().add(cref);
//        PojoService sourceService = new PojoService();
//        sourceService.setServiceContract(inter);
//        sourceService.setName("GenericComponent");
//        PojoConfiguredService cService = new PojoConfiguredService();
//        cService.setService(sourceService);
//        //cService.setPart(targetComponent);
//        //cService.setPort(targetService);
//
//        sourceComponent.getComponentImplementation().getServices().add(sourceService);
//        sourceComponent.getConfiguredServices().add(cService);
//
//        PojoService targetService = new PojoService();
//        targetService.setServiceContract(inter);
//        targetService.setName("GenericComponent");
//        PojoConfiguredService cTargetService = new PojoConfiguredService();
//        cTargetService.setService(targetService);
//        //cTargetService.setPart(targetComponent);
//       // cTargetService.setPort(targetService);
//        targetComponent.getComponentImplementation().getServices().add(targetService);
//        targetComponent.getConfiguredServices().add(cTargetService);
//
//        PojoModule module = new PojoModule();
//        module.setName("test.module");
//        module.addComponent(sourceComponent);
//        module.addComponent(targetComponent);
//        return module;
//    }

}

