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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test ClassReferenceArtifactResolver.
 *
 * @version $Rev$ $Date$
 */
public class ClassReferenceArtifactResolverTestCase {
    private static ExtensibleModelResolver resolver;
    
    @BeforeClass
    public static void setUp() throws Exception {
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        
        ModelResolverExtensionPoint resolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        resolvers.addResolver(ClassReference.class, ClassReferenceModelResolver.class);
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        resolver = new ExtensibleModelResolver(null, resolvers, modelFactories);
    }
    
    /**
     * Test ClassReference resolution
     * 
     */
    @Test
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
    @Test
    public void testUnresolvedClass() {
        ClassReference ref = new ClassReference("NonExistentClass");
        ClassReference clazz = resolver.resolveModel(ClassReference.class, ref);
        assertTrue(clazz.isUnresolved());
        assertTrue(clazz.getJavaClass() == null);
    }
}
