/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.config.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Set;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.MetaDataException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.ComponentType;
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

    public void visitEnd(Class<?> clazz, ComponentType type) throws ConfigurationLoadException {
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
