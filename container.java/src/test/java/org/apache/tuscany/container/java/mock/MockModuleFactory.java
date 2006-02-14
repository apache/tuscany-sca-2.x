package org.apache.tuscany.container.java.mock;

import java.lang.reflect.Method;

import org.apache.tuscany.container.java.assembly.pojo.PojoJavaOperationType;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.container.java.mock.components.ModuleScopeComponentImpl;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.assembly.pojo.PojoConfiguredReference;
import org.apache.tuscany.model.assembly.pojo.PojoConfiguredService;
import org.apache.tuscany.model.assembly.pojo.PojoInterface;
import org.apache.tuscany.model.assembly.pojo.PojoInterfaceType;
import org.apache.tuscany.model.assembly.pojo.PojoJavaInterface;
import org.apache.tuscany.model.assembly.pojo.PojoModule;
import org.apache.tuscany.model.assembly.pojo.PojoPort;
import org.apache.tuscany.model.assembly.pojo.PojoReference;
import org.apache.tuscany.model.assembly.pojo.PojoService;

public class MockModuleFactory {

    private MockModuleFactory() {
    }

    public static Module createModule() throws Exception {
        Component sourceComponent = MockAssemblyFactory.createComponent("source", ModuleScopeComponentImpl.class,
                ScopeEnum.MODULE_LITERAL);
        Component targetComponent = MockAssemblyFactory.createComponent("target", ModuleScopeComponentImpl.class,
                ScopeEnum.MODULE_LITERAL);
        PojoReference ref = new PojoReference();
        PojoConfiguredReference cref = new PojoConfiguredReference();
        ref.setName("setGenericComponent");
        PojoInterface inter = new PojoJavaInterface();
        PojoInterfaceType type = new PojoInterfaceType();
        type.setInstanceClass(GenericComponent.class);
        PojoJavaOperationType oType = new PojoJavaOperationType();
        oType.setName("getString");
        oType.setJavaMethod((Method) JavaIntrospectionHelper.getBeanProperty(GenericComponent.class, "getString", null));
        type.addOperationType(oType);
        inter.setInterfaceType(type);
        ref.setInterfaceContract(inter);
        cref.setReference(ref);
        cref.setPart(targetComponent);
        PojoPort port = new PojoPort();
        port.setName("GenericComponent");
        cref.setPort(port);
        sourceComponent.getConfiguredReferences().add(cref);
        PojoService sourceService = new PojoService();
        sourceService.setInterfaceContract(inter);
        sourceService.setName("GenericComponent");
        PojoConfiguredService cService = new PojoConfiguredService();
        cService.setService(sourceService);
        //cService.setPart(targetComponent);
        //cService.setPort(targetService);

        sourceComponent.getComponentImplementation().getServices().add(sourceService);
        sourceComponent.getConfiguredServices().add(cService);

        PojoService targetService = new PojoService();
        targetService.setInterfaceContract(inter);
        targetService.setName("GenericComponent");
        PojoConfiguredService cTargetService = new PojoConfiguredService();
        cTargetService.setService(targetService);
        //cTargetService.setPart(targetComponent);
       // cTargetService.setPort(targetService);
        targetComponent.getComponentImplementation().getServices().add(targetService);
        targetComponent.getConfiguredServices().add(cTargetService);

        PojoModule module = new PojoModule();
        module.setName("test.module");
        module.addComponent(sourceComponent);
        module.addComponent(targetComponent);
        return module;
    }

}

