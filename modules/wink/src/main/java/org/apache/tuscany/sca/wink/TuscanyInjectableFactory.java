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

package org.apache.tuscany.sca.wink;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

import org.apache.wink.common.internal.registry.Injectable;
import org.apache.wink.server.internal.registry.ServerInjectableFactory;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;

/**
 * Subclass the Wink default ServerInjectableFactory to look
 * for SCA @Reference or @Property annotations. 
 */
public class TuscanyInjectableFactory extends ServerInjectableFactory {

    @Override
    public Injectable create(Type genericType,
                             Annotation[] annotations,
                             Member member,
                             boolean encoded,
                             String defaultValue) {
        for (Annotation a : annotations) {
            if (a instanceof Reference) {
                return new ReferenceInjectable((Reference)a, null, null, genericType, annotations, member);
            } else if (a instanceof Property) {
                return new PropertyInjectable((Property)a, null, null, genericType, annotations, member);
            }
        }
        return super.create(genericType, annotations, member, encoded, defaultValue);
    }
}
