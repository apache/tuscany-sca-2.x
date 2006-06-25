package org.apache.tuscany.core.implementation.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.DeploymentContext;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getBaseName;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.toPropertyName;

/**
 * Processes an {@link @Reference} annotation, updating the component type with corresponding {@link
 * org.apache.tuscany.core.implementation.JavaMappedReference}
 *
 * @version $Rev$ $Date$
 */
public class ReferenceProcessor extends ImplementationProcessorSupport {

    public void visitMethod(Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context)
        throws ProcessingException {
        Reference annotation = method.getAnnotation(Reference.class);
        if (annotation == null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            IllegalReferenceException e = new IllegalReferenceException("Setter must have one parameter");
            e.setIdentifier(method.getName());
            throw e;
        }
        String name = annotation.name();
        if (name.length() == 0) {
            if (method.getName().startsWith("set")) {
                name = toPropertyName(method.getName());
            } else {
                name = method.getName();
            }
        }
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        JavaMappedReference reference = new JavaMappedReference();
        reference.setMember(method);
        reference.setRequired(annotation.required());
        JavaServiceContract contract = new JavaServiceContract();
        Class<?> interfaceType = method.getParameterTypes()[0];
        String interfaceName = getBaseName(interfaceType);
        contract.setInterfaceName(interfaceName);
        contract.setInterfaceClass(interfaceType);
        reference.setServiceContract(contract);
        type.getReferences().put(name, reference);
    }

    public void visitField(Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {
        Reference annotation = field.getAnnotation(Reference.class);
        if (annotation == null) {
            return;
        }
        String name = annotation.name();
        if (name.length() == 0) {
            name = field.getName();
        }
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        JavaMappedReference reference = new JavaMappedReference();
        reference.setMember(field);
        reference.setRequired(annotation.required());
        JavaServiceContract contract = new JavaServiceContract();
        Class<?> interfaceType = field.getType();
        String interfaceName = getBaseName(interfaceType);
        contract.setInterfaceName(interfaceName);
        contract.setInterfaceClass(interfaceType);
        reference.setServiceContract(contract);
        type.getReferences().put(name, reference);
    }

}
