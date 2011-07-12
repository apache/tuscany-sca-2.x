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
package org.apache.tuscany.sca.implementation.java.injection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;

/**
 * Resolves targets configured in a multiplicity by delegating to object factories and returning an <code>List</code>
 * containing object instances
 *
 * @version $Rev$ $Date$
 */
public class ListMultiplicityObjectFactory implements ObjectFactory<Collection<?>> {

    private Collection<ObjectFactory<?>> factories;
    private Class<?> collectionType;

    public ListMultiplicityObjectFactory(List<ObjectFactory<?>> factories, Class<?> collectionType) {
        assert factories != null : "Object factories were null";
        this.factories = factories;
        this.collectionType = collectionType;
    }

    public Collection<?> getInstance() throws ObjectCreationException {
        Collection<Object> collection = null;
        if (SortedSet.class.isAssignableFrom(collectionType)) {
            collection = new TreeSet<Object>();
        } else if (Set.class.isAssignableFrom(collectionType)) {
            collection = new HashSet<Object>();
        } else if (List.class.isAssignableFrom(collectionType)) {
            collection = new ArrayList<Object>();
        } else {
            collection = new ArrayList<Object>();
        }
        for (ObjectFactory<?> factory : factories) {
            collection.add(factory.getInstance());
        }
        return collection;
    }

}
