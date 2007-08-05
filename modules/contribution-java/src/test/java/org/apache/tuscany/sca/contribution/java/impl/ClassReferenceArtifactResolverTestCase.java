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

package org.apache.tuscany.sca.contribution.java.impl;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.DefaultModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;

/**
 * Test ClassReferenceArtifactResolver.
 *
 * @version $Rev: 560435 $ $Date: 2007-07-27 18:26:55 -0700 (Fri, 27 Jul 2007) $
 */
public class ClassReferenceArtifactResolverTestCase extends TestCase {
    private ExtensibleModelResolver resolver;
    
    protected void setUp() throws Exception {
        
        ModelResolverExtensionPoint resolvers = new DefaultModelResolverExtensionPoint();
        resolvers.addResolver(ClassReference.class, ClassReferenceModelResolver.class);
        
        ModelFactoryExtensionPoint factories = new DefaultModelFactoryExtensionPoint();
        
        resolver = new ExtensibleModelResolver(null, resolvers, factories);
    }
    
    protected void tearDown() throws Exception {
    }
    
    /**
     * Test ClassReference resolution
     * 
     */
    public void testResolveClass() {
        ClassReference ref = new ClassReference(getClass().getName());
        ClassReference clazz = resolver.resolveModel(ClassReference.class, ref);
        assertFalse(clazz.isUnresolved());
        assertTrue(clazz.getJavaClass() == getClass());
    }
    
    /**
     * Test ClassReference resolution of inexistent class
     *
     */
    public void testUnresolvedClass() {
        ClassReference ref = new ClassReference("NonExistentClass");
        ClassReference clazz = resolver.resolveModel(ClassReference.class, ref);
        assertTrue(clazz.isUnresolved());
        assertTrue(clazz.getJavaClass() == null);
    }
}
