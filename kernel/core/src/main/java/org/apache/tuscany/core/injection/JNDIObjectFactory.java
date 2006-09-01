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
package org.apache.tuscany.core.injection;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * An implementation of ObjectFactory that creates instances by looking them up in a JNDI context.
 *
 * @version $Rev$ $Date$
 */
public class JNDIObjectFactory<T> implements ObjectFactory<T> {
    private final Context context;
    private final String name;

    public JNDIObjectFactory(Context context, String name) {
        this.context = context;
        this.name = name;
    }


    @SuppressWarnings("unchecked")
    public T getInstance() throws ObjectCreationException {
        try {
            return (T) context.lookup(name);
        } catch (NamingException e) {
            throw new ObjectCreationException(e);
        }
    }
}
