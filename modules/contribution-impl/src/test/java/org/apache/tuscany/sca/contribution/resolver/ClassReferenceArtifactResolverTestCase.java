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

package org.apache.tuscany.sca.contribution.resolver;

import junit.framework.TestCase;

/**
 * Test ClassReferenceArtifactResolver.
 *
 * @version $Rev: 560435 $ $Date: 2007-07-27 18:26:55 -0700 (Fri, 27 Jul 2007) $
 */
public class ClassReferenceArtifactResolverTestCase extends TestCase {
    private ModelResolverExtensionPoint resolverExtensionPoint;
    private ExtensibleModelResolver resolver;
    
    protected void setUp() throws Exception {
        
        resolverExtensionPoint = new DefaultModelResolverExtensionPoint();
        resolverExtensionPoint.addResolver(ClassReference.class, ClassReferenceModelResolver.class);
        
        resolver = new ExtensibleModelResolver(null, resolverExtensionPoint);
    }
    
    protected void tearDown() throws Exception {
        resolverExtensionPoint.removeResolver(ClassReference.class);
        resolverExtensionPoint = null;
        resolver = null;
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
}
