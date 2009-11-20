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

import static org.junit.Assert.assertTrue;

import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the default model resolver implementation.
 *
 * @version $Rev$ $Date$
 */
public class DefaultModelResolverTestCase {

    private ModelResolver resolver;
    private ContributionFactory factory;

    @Before
    public void setUp() throws Exception {
        resolver = new DefaultModelResolver();
        factory = new DefaultContributionFactory();
    }

    @Test
    public void testResolved() {
        ProcessorContext context = new ProcessorContext();
        Model a = new Model("a");
        resolver.addModel(a, context);
        Model x = new Model("a");
        x = resolver.resolveModel(Model.class, x, context);
        assertTrue(x == a);
    }

    @Test
    public void testUnresolved() {
        Model x = new Model("a");
        Model y = resolver.resolveModel(Model.class, x, new ProcessorContext());
        assertTrue(x == y);
    }

    @Test
    public void testResolvedArtifact() {
        ProcessorContext context = new ProcessorContext();
        Artifact artifact = factory.createArtifact();
        artifact.setURI("foo/bar");
        resolver.addModel(artifact, context);
        Artifact x = factory.createArtifact();
        x.setURI("foo/bar");
        x = resolver.resolveModel(Artifact.class, x, context);
        assertTrue(x == artifact);
    }

    class Model {
        private String name;

        Model(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return name.equals(((Model)obj).name);
        }
    }

}
