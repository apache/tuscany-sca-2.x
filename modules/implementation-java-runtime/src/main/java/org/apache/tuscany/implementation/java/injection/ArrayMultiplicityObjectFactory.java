/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.implementation.java.injection;

import java.lang.reflect.Array;
import java.util.List;

import org.apache.tuscany.sca.spi.ObjectCreationException;
import org.apache.tuscany.sca.spi.ObjectFactory;

/**
 * Resolves targets configured in a multiplicity by delegating to object factories and returning an <code>Array</code>
 * containing object instances
 *
 * @version $Rev$ $Date$
 */
public class ArrayMultiplicityObjectFactory implements ObjectFactory<Object> {

    private ObjectFactory[] factories;

    private Class interfaceType;

    public ArrayMultiplicityObjectFactory(Class interfaceType, List<ObjectFactory<?>> factories) {
        assert interfaceType != null : "Interface type was null";
        assert factories != null : "Object factories were null";
        this.interfaceType = interfaceType;
        this.factories = factories.toArray(new ObjectFactory[factories.size()]);
    }

    public Object getInstance() throws ObjectCreationException {
        Object array = Array.newInstance(interfaceType, factories.length);
        for (int i = 0; i < factories.length; i++) {
            Array.set(array, i, factories[i].getInstance());
        }
        return array;
    }

}
