/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.sca.metadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.osoa.sca.annotations.Property;

/**
 * @author Rod Johnson
 */
public class MethodInjection extends Injection {

    private final Method method;

    public MethodInjection(Method method, String lookupName) {
        super(lookupName);
        this.method = method;
    }

    public MethodInjection(Method method) {
        // TODO reference also
        Property annotation = method.getAnnotation(Property.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Method " + method + " not annotated");
        }
        this.method = method;
        if ("".equals(annotation.name())) {
            setLookupName(method.getName());
        } else {
            setLookupName(annotation.name());
        }
    }

    @Override
    protected void injectValue(Object target, Object value) {
        try {
            method.invoke(target, value);
        } catch (IllegalArgumentException ex) {
            // TODO
            throw new UnsupportedOperationException();
        } catch (IllegalAccessException ex) {
            // TODO
            throw new UnsupportedOperationException();
        } catch (InvocationTargetException ex) {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

}
