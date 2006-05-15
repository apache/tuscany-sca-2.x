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
package org.apache.tuscany.core.extension.config.extensibility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.extension.config.JavaExtensibilityElement;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ComponentNameExtensibilityElement implements JavaExtensibilityElement {

    private Method method;
    private Field field;

    public ComponentNameExtensibilityElement(Method m) {
        method = m;
    }

    public ComponentNameExtensibilityElement(Field f) {
        field = f;
    }

    public Injector<?> getEventInvoker(String name) {
        if (method != null) {
            return new MethodInjector(method, new SingletonObjectFactory<Object>(name));
        }else{
            return new FieldInjector(field, new SingletonObjectFactory<Object>(name));
        }
    }

}
