package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.component.CompositeComponent;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;

/**
 * Processes the {@link Scope} annotation and updates the component type with the corresponding implmentation scope
 *
 * @version $Rev$ $Date$
 */
public class ScopeProcessor extends ImplementationProcessorSupport {

    public void visitClass(CompositeComponent<?> parent, Class<?> clazz,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context)
        throws ProcessingException {
        org.osoa.sca.annotations.Scope annotation = clazz.getAnnotation(org.osoa.sca.annotations.Scope.class);
        if (annotation == null) {
            type.setLifecycleScope(Scope.STATELESS);
            return;
        }
        //FIXME deal with eager init
        //FIXME needs to be extensible
        String name = annotation.value();
        Scope scope;
        if ("MODULE".equals(name)) {
            scope = Scope.MODULE;
        } else if ("SESSION".equals(name)) {
            scope = Scope.SESSION;
        } else if ("REQUEST".equals(name)) {
            scope = Scope.REQUEST;
        } else if ("COMPOSITE".equals(name)) {
            scope = Scope.COMPOSITE;
        } else {
            scope = Scope.STATELESS;
        }
        type.setLifecycleScope(scope);
    }
}
