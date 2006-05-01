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
package org.apache.tuscany.core.system.config.extensibility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.system.config.SystemInjectorExtensibilityElement;
import org.apache.tuscany.core.system.injection.AutowireObjectFactory;

/**
 * A metadata extensbility element for autowires; creates injectors which return the target of an autowire
 *
 * @version $$Rev$$ $$Date$$
 */
public class AutowireExtensibilityElement implements SystemInjectorExtensibilityElement {

    private Method method;
    private Field field;

    public AutowireExtensibilityElement(Method m) {
        assert(method.getParameterTypes().length == 1): "Illegal number of parameters";
        method = m;
    }

    public AutowireExtensibilityElement(Field f) {
        field = f;
    }

    @SuppressWarnings("unchecked")
    public Injector<?> getInjector(ContextResolver resolver) {
        if (method != null) {
            return new MethodInjector(method, new AutowireObjectFactory(method.getParameterTypes()[0], resolver));
        } else {
            return new FieldInjector(field, new AutowireObjectFactory(field.getType(), resolver));
        }
    }
}
