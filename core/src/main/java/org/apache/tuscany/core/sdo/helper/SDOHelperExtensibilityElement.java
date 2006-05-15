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

import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.extension.config.InjectorExtensibilityElement;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.sdo.DataFactoryObjectFactory;
import org.apache.tuscany.core.sdo.TypeHelperObjectFactory;
import org.apache.tuscany.core.sdo.XMLHelperObjectFactory;
import org.apache.tuscany.core.sdo.XSDHelperObjectFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SDOHelperExtensibilityElement implements InjectorExtensibilityElement {

    private Method method;
    private Field field;
    private Class<?> type;

    public SDOHelperExtensibilityElement(Method m) {
        method = m;
        assert(method != null);
        assert(method.getParameterTypes().length == 1);
        type = method.getParameterTypes()[0];
    }

    public SDOHelperExtensibilityElement(Field field) {
        assert (field != null);
        this.field = field;
        this.type = field.getType();
    }

    public Injector<?> getInjector(ContextResolver resolver) {
        ObjectFactory<?> factory;
        if (TypeHelper.class.equals(type)) {
            factory = new TypeHelperObjectFactory(resolver);
        } else if (DataFactory.class.equals(type)) {
            factory = new DataFactoryObjectFactory(resolver);
        } else if (XSDHelper.class.equals(type)) {
            factory = new XSDHelperObjectFactory(resolver);
        } else if (XMLHelper.class.equals(type)) {
            factory = new XMLHelperObjectFactory(resolver);
        } else {
            ObjectCreationException e = new ObjectCreationException("Unknown type");
            e.setIdentifier(type.getName());
            throw e;
        }
        if (method != null) {
            return new MethodInjector(method, factory);
        }
        return new FieldInjector(field, factory);
    }
}
