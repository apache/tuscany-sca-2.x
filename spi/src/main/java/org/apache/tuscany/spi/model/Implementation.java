/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.spi.model;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

import org.apache.tuscany.spi.model.ComponentType;

/**
 * @version $Rev$ $Date$
 */
public abstract class Implementation<T extends ComponentType> extends ModelObject {
    private T componentType;

    public T getComponentType() {
        return componentType;
    }

    public void setComponentType(T componentType) {
        this.componentType = componentType;
    }

    public Class<T> getComponentTypeClass(){
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];

        }
        return null;
    }
}
