package org.apache.tuscany.core.implementation.processor;

import static org.apache.tuscany.core.implementation.processor.ProcessorUtils.processCallback;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.core.implementation.ImplementationProcessorSupport;
import org.apache.tuscany.core.implementation.JavaMappedProperty;
import org.apache.tuscany.core.implementation.JavaMappedReference;
import org.apache.tuscany.core.implementation.JavaMappedService;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getBaseName;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.toPropertyName;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ServiceContract;

/**
 * Processes an {@link @Reference} annotation, updating the component type with corresponding {@link
 * org.apache.tuscany.core.implementation.JavaMappedReference}
 *
 * @version $Rev$ $Date$
 */
public class ReferenceProcessor extends ImplementationProcessorSupport {

    public void visitMethod(CompositeComponent<?> parent, Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context)
        throws ProcessingException {
        Reference annotation = method.getAnnotation(Reference.class);
        boolean autowire = method.getAnnotation(Autowire.class) != null;
        if (annotation == null && !autowire) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            IllegalReferenceException e = new IllegalReferenceException("Setter must have one parameter");
            e.setIdentifier(method.toString());
            throw e;
        }
        String name = null;
        boolean required = false;
        if (annotation != null) {
            if (annotation.name() != null && annotation.name().length() > 0) {
                name = annotation.name();
            }
            required = annotation.required();
        }
        if (name == null) {
            name = method.getName();
            if (method.getName().startsWith("set")) {
                name = toPropertyName(method.getName());
            }
        }
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        JavaMappedReference reference = new JavaMappedReference();
        reference.setMember(method);
        reference.setAutowire(autowire);
        reference.setRequired(required);
        reference.setName(name);
        ServiceContract contract = new JavaServiceContract();
        Class<?> interfaceType = method.getParameterTypes()[0];
        String interfaceName = getBaseName(interfaceType);
        contract.setInterfaceName(interfaceName);
        contract.setInterfaceClass(interfaceType);
        reference.setServiceContract(contract);
        type.getReferences().put(name, reference);
        processCallback(interfaceType, contract);
    }

    public void visitField(CompositeComponent<?> parent, Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {
        Reference annotation = field.getAnnotation(Reference.class);
        boolean autowire = field.getAnnotation(Autowire.class) != null;
        if (annotation == null && !autowire) {
            return;
        }
        String name = field.getName();
        boolean required = false;
        if (annotation != null) {
            if (annotation.name() != null) {
                name = annotation.name();
            }
            required = annotation.required();
        }
        if (name.length() == 0) {
            name = field.getName();
        }
        if (type.getReferences().get(name) != null) {
            throw new DuplicateReferenceException(name);
        }
        JavaMappedReference reference = new JavaMappedReference();
        reference.setMember(field);
        reference.setRequired(required);
        reference.setAutowire(autowire);
        reference.setName(name);
        ServiceContract contract = new JavaServiceContract();
        Class<?> interfaceType = field.getType();
        String interfaceName = getBaseName(interfaceType);
        contract.setInterfaceName(interfaceName);
        contract.setInterfaceClass(interfaceType);
        reference.setServiceContract(contract);
        type.getReferences().put(name, reference);
        processCallback(interfaceType, contract);

    }

    public void visitConstructor(CompositeComponent<?> parent, Constructor<?> constructor,
                                 PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                 DeploymentContext context) throws ProcessingException {

    }



}
