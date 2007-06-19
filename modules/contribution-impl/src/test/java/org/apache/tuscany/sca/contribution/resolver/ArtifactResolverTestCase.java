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

import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.impl.ContributionFactoryImpl;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;

/**
 * Test DefaultArtifactResolver.
 *
 * @version $Rev$ $Date$
 */
public class ArtifactResolverTestCase extends TestCase {
    
    private ModelResolver resolver;
    private ContributionFactory factory;
    
    protected void setUp() throws Exception {
        resolver = new ModelResolverImpl(getClass().getClassLoader());
        factory = new ContributionFactoryImpl();
    }
    
    protected void tearDown() throws Exception {
        resolver = null;
        factory = null;
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
    
    public void testResolvedArtifact() {
        DeployedArtifact artifact = factory.createDeployedArtifact();
        artifact.setURI("foo/bar");
        resolver.addModel(artifact);
        DeployedArtifact x = factory.createDeployedArtifact();
        x.setURI("foo/bar");
        x = resolver.resolveModel(DeployedArtifact.class, x);
        assertTrue(x == artifact);
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
