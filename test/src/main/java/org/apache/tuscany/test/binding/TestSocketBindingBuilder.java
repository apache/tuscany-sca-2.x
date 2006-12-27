package org.apache.tuscany.test.binding;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

/**
 * @version $Rev$ $Date$
 */
public class TestSocketBindingBuilder extends BindingBuilderExtension<TestSocketBindingDefinition> {

    public Service build(CompositeComponent parent,
                         BoundServiceDefinition<TestSocketBindingDefinition> definition,
                         DeploymentContext context) {
        int port = definition.getBinding().getPort();
        return new TestSocketBindingService(definition.getName(), port, parent);
    }

    public Reference build(CompositeComponent parent,
                           BoundReferenceDefinition<TestSocketBindingDefinition> definition,
                           DeploymentContext context) {
        String name = definition.getName();
        int port = definition.getBinding().getPort();
        String host = definition.getBinding().getHost();
        return new TestSocketBindingReference(name, host, port, parent);
    }

    protected Class<TestSocketBindingDefinition> getBindingType() {
        return TestSocketBindingDefinition.class;
    }
}
