package org.apache.tuscany.core.config.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Set;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.MetaDataException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * Validates the use of {@link org.osoa.sca.annotations.Property} and {@link
 * org.osoa.sca.annotations.Reference} annotations beyond native Java syntactic capabilities
 *
 * @version $$Rev$$ $$Date$$
 */
public class PropertyReferenceValidator extends ImplementationProcessorSupport {

    public PropertyReferenceValidator(AssemblyFactory factory) {
        super(factory);
    }

    public void visitEnd(Class<?> clazz, ComponentInfo type) throws ConfigurationLoadException {
        // validate methods do not contain both @Reference and @Property annotations
        Method[] methods = clazz.getMethods();
        boolean found;
        for (Method method : methods) {
            found = false;
            Annotation[] anotations = method.getAnnotations();
            for (Annotation annotation : anotations) {
                if (Property.class.equals(annotation.annotationType())
                        || Reference.class.equals(annotation.annotationType())) {
                    if (found) {
                        MetaDataException e = new MetaDataException("Method cannot specify both property and reference");
                        e.setIdentifier(method.getName());
                        throw e;
                    }
                    found = true;
                }
            }
        }
        // validate fields do not contain both @Reference and @Property annotations
        Set<Field> fields = JavaIntrospectionHelper.getAllPublicAndProtectedFields(clazz);
        for (Field field : fields) {
            found = false;
            Annotation[] anotations = field.getAnnotations();
            for (Annotation annotation : anotations) {
                if (Property.class.equals(annotation.annotationType())
                        || Reference.class.equals(annotation.annotationType())) {
                    if (found) {
                        MetaDataException e = new MetaDataException("Field cannot specify both property and reference");
                        e.setIdentifier(field.getName());
                        throw e;
                    }
                    found = true;
                }
            }
        }

    }

}
