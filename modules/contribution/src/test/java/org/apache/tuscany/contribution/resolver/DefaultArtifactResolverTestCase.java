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

package org.apache.tuscany.contribution.resolver;

import junit.framework.TestCase;

/**
 * Test DefaultArtifactResolver.
 *
 * @version $Rev$ $Date$
 */
public class DefaultArtifactResolverTestCase extends TestCase {
    
    private ModelResolver resolver;
    
    protected void setUp() throws Exception {
        resolver = new DefaultModelResolver(getClass().getClassLoader());
    }
    
    protected void tearDown() throws Exception {
        resolver = null;
    }
    
    public void testResolved() {
        Model a = new Model("a");
        resolver.addModel(a);
        Model x = new Model("a");
        x = resolver.resolveModel(Model.class, x);
        assertTrue(x == a);
    }
    
    public void testUnresolved() {
        Model x = new Model("a");
        Model y = resolver.resolveModel(Model.class, x);
        assertTrue(x == y);
    }
    
    public void testResolveClass() {
        ClassReference ref = new ClassReference(getClass().getName());
        ClassReference clazz = resolver.resolveModel(ClassReference.class, ref);
        assertTrue(clazz.getJavaClass() == getClass());
    }
    
    public void testUnresolvedClass() {
        ClassReference ref = new ClassReference("NonExistentClass");
        ClassReference clazz = resolver.resolveModel(ClassReference.class, ref);
        assertTrue(clazz.isUnresolved());
        assertTrue(clazz.getJavaClass() == null);
    }
    
    
    
    class Model {
        private String name;
        
        Model(String name) {
            this.name = name;
        }
        
        public int hashCode() {
            return name.hashCode();
        }
        
        public boolean equals(Object obj) {
            return name.equals(((Model)obj).name);
        }
    }

}
