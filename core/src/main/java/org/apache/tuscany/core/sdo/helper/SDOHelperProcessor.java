/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.sdo.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.InvalidSetterException;
import org.apache.tuscany.core.config.processor.ImplementationProcessorSupport;
import org.apache.tuscany.model.assembly.ComponentType;
import org.osoa.sca.annotations.Scope;

/**
 * @version $$Rev$$ $$Date$$
 */
@Scope("Module")
public class SDOHelperProcessor extends ImplementationProcessorSupport {

    @Override
    public void visitMethod(Method method, ComponentType type) throws ConfigurationLoadException {
        if (method.getDeclaringClass().equals(Object.class)) {
            return;
        }
        SDOHelper annotation = method.getAnnotation(SDOHelper.class);
        if (annotation != null) {
            if (!Modifier.isPublic(method.getModifiers())) {
                InvalidSetterException e = new InvalidSetterException("SDO setter method is not public");
                e.setIdentifier(method.toString());
                throw e;
            }
            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) {
                InvalidSetterException e = new InvalidSetterException("SDO setter method must have one parameter");
                e.setIdentifier(method.toString());
                throw e;
            }
            type.getExtensibilityElements().add(new SDOHelperExtensibilityElement(method));

        }


    }

    @Override
    public void visitField(Field field, ComponentType type) throws ConfigurationLoadException {
        if (field.getDeclaringClass().equals(Object.class)) {
            return;
        }
        int modifiers = field.getModifiers();
        SDOHelper annotation = field.getAnnotation(SDOHelper.class);
        if (annotation != null) {
            if (!Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers)) {
                InvalidSetterException e = new InvalidSetterException("Property field is not public or protected");
                e.setIdentifier(field.getName());
                throw e;
            }
            type.getExtensibilityElements().add(new SDOHelperExtensibilityElement(field));
        }
    }


}
