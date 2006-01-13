package org.apache.tuscany.core.builder.impl;

import java.util.Collections;
import java.util.List;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.TuscanyModuleComponentContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.SimpleComponent;

/**
 * Generates runtime configurations for logical model artifacts contained in a module component such as a
 * <code>SimpleComponent</code>
 * 
 * @version $Rev$ $Date$
 */
public class TuscanyModuleContextBuilder implements RuntimeConfigurationBuilder<TuscanyModuleComponentContext> {

    private AssemblyModelObject modelObject;

    private TuscanyModuleComponentContext moduleComponentContext;

    // a collection of builders that will visit the contained artifacts when the
    // logical model is walked
    private List<RuntimeConfigurationBuilder<TuscanyModuleComponentContext>> componentBuilders;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    // TODO add different builder types
    public TuscanyModuleContextBuilder(List<RuntimeConfigurationBuilder<TuscanyModuleComponentContext>> componentBuilders) {
        if (componentBuilders != null) {
            this.componentBuilders = componentBuilders;
        } else {
            this.componentBuilders = Collections.emptyList();
        }
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public void setModelObject(AssemblyModelObject modelObject) {
        this.modelObject = modelObject;
    }

    public void setParentContext(TuscanyModuleComponentContext context) {
        moduleComponentContext = context;
        for (RuntimeConfigurationBuilder<TuscanyModuleComponentContext> builder : componentBuilders) {
            builder.setParentContext(context);
        }
    }

    public void build() throws BuilderException {
        if(!(modelObject instanceof ModuleComponent)){
            return;
        }
        ModuleComponent moduleComponent = (ModuleComponent)modelObject;

        // FIXME find a better way to handle builders and visit nodes
        List<Component> components = moduleComponent.getModuleImplementation().getComponents();
        for (Component component : components) {
            // FIXME cast
            if (component instanceof SimpleComponent) {
                try {
                    for (RuntimeConfigurationBuilder<TuscanyModuleComponentContext> builder : componentBuilders) {
                        builder.setModelObject((SimpleComponent) component);
                        builder.build();
                    }
                } catch (BuilderException e) {
                    e.addContextName(component.getName());
                    e.addContextName(moduleComponent.getName());
                    throw e;
                }
            }
        }
    }


}
