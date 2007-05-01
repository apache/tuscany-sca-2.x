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
package org.apache.tuscany.implementation.java.impl;

/**
 * The default implementation scopes supported by assemblies.
 *
 * @version $Rev$ $Date$
 */
public class JavaScopeImpl {
    public static final JavaScopeImpl STATELESS = new JavaScopeImpl("STATELESS");
    public static final JavaScopeImpl REQUEST = new JavaScopeImpl("REQUEST");
    public static final JavaScopeImpl SESSION = new JavaScopeImpl("SESSION");
    public static final JavaScopeImpl CONVERSATION = new JavaScopeImpl("CONVERSATION");
    public static final JavaScopeImpl COMPOSITE = new JavaScopeImpl("COMPOSITE");
    public static final JavaScopeImpl SYSTEM = new JavaScopeImpl("SYSTEM");
    public static final JavaScopeImpl UNDEFINED = new JavaScopeImpl("UNDEFINED");

    private String scope;

    public JavaScopeImpl(String scope) {
        this.scope = scope.toUpperCase().intern();
    }

    public String getScope() {
        return scope;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final JavaScopeImpl scope1 = (JavaScopeImpl) o;
        return !(scope != null ? scope != scope1.scope.intern() : scope1.scope != null);
    }

    public int hashCode() {
        return scope != null ? scope.hashCode() : 0;
    }

    public String toString() {
        return scope;
    }
}
