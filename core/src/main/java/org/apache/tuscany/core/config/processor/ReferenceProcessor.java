package org.apache.tuscany.core.config.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.InvalidSetterException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.osoa.sca.annotations.Scope;

/**
 * Processes the {@link org.osoa.sca.annotations.Reference} annotation
 *
 *  @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
public class ReferenceProcessor extends ImplementationProcessorSupport {

    public ReferenceProcessor() {
    }

    public ReferenceProcessor(AssemblyFactory factory) {
        super(factory);
    }

    @Override
    public void visitMethod(Method method, ComponentInfo type) throws ConfigurationLoadException {
        if(method.getDeclaringClass().equals(Object.class)){
            return;
        }
        int modifiers = method.getModifiers();
        org.osoa.sca.annotations.Reference annotation = method.getAnnotation(org.osoa.sca.annotations.Reference.class);
        if (annotation != null) {
            if (!Modifier.isPublic(modifiers)) {
                InvalidSetterException e = new InvalidSetterException("Reference setter method is not public");
                e.setIdentifier(method.getName());
                throw e;
            }
            if (!Void.TYPE.equals(method.getReturnType())) {
                InvalidSetterException e = new InvalidSetterException("Refence setter method must return void");
                e.setIdentifier(method.toString());
                throw e;
            }
            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) {
                InvalidSetterException e = new InvalidSetterException("Reference setter method must have one parameter");
                e.setIdentifier(method.toString());
                throw e;
            }
            String name = annotation.name();
            if (name.length() == 0) {
                name = method.getName();
                if (name.length() > 3 && name.startsWith("set")) {
                    // follow JavaBeans conventions
                    name = JavaIntrospectionHelper.toPropertyName(name);
                }
            }
            addReference(name, params[0], annotation.required(), type);
        }
    }

    @Override
    public void visitField(Field field, ComponentInfo type) throws ConfigurationLoadException {
        if(field.getDeclaringClass().equals(Object.class)){
            return;
        }
        int modifiers = field.getModifiers();
        org.osoa.sca.annotations.Reference annotation = field.getAnnotation(org.osoa.sca.annotations.Reference.class);
        if (annotation != null) {
            if (!Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers)) {
                InvalidSetterException e = new InvalidSetterException("Reference field is not public or protected");
                e.setIdentifier(field.getName());
                throw e;
            }
            String name = annotation.name();
            if (name.length() == 0) {
                name = field.getName();
            }
            addReference(name, field.getType(), annotation.required(), type);
        }
    }

    private void addReference(String name, Class<?> paramType, boolean required, ComponentInfo type) {
        Reference reference = factory.createReference();
        reference.setName(name);
        ServiceContract contract = factory.createJavaServiceContract();
        contract.setInterface(paramType);
        reference.setServiceContract(contract);
        boolean many = paramType.isArray() || Collection.class.isAssignableFrom(paramType);
        Multiplicity multiplicity;
        if (required)
            multiplicity = many ? Multiplicity.ONE_N : Multiplicity.ONE_ONE;
        else
            multiplicity = many ? Multiplicity.ZERO_N : Multiplicity.ZERO_ONE;
        reference.setMultiplicity(multiplicity);
        type.getReferences().add(reference);
    }

}
