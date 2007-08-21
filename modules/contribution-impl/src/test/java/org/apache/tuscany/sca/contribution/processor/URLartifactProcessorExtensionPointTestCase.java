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

package org.apache.tuscany.sca.contribution.processor;

import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;


/**
 * URL Artifact Processor Extension Point test case
 * Verifies the right registration and lookup for processors that handle filename and file types
 * 
 * @version $Rev$ $Date$
 */
public class URLartifactProcessorExtensionPointTestCase extends TestCase {
    URLArtifactProcessorExtensionPoint artifactProcessors;
    
    @Override
    protected void setUp() throws Exception {
        artifactProcessors = new DefaultURLArtifactProcessorExtensionPoint(null);
        artifactProcessors.addArtifactProcessor(new FileTypeArtifactProcessor());
        artifactProcessors.addArtifactProcessor(new FileNameArtifactProcessor());
    }
    
    
    public final void testFileTypeProcessor() {
        URLArtifactProcessor processor = null;
        
        processor = artifactProcessors.getProcessor(".m1");
        assertNotNull(processor);
    }
    
    
    public final void testFileNameProcessor() {
        URLArtifactProcessor processor = null;
        
        processor = artifactProcessors.getProcessor("file.m2");
        assertNotNull(processor);
        
    }
    
    @Override
    protected void tearDown() throws Exception {
    }
    
    /**
     * Internal mock classes
     *
     */
    
    class M1 {
        
    }
    
    class M2 {
        
    }
    
    class FileTypeArtifactProcessor implements URLArtifactProcessor<M1> {
        public FileTypeArtifactProcessor() {
        }

        public M1 read(URL contributionURL, URI uri, URL url) throws ContributionReadException {
            return null;
        }
        
        public void resolve(M1 m1, ModelResolver resolver) throws ContributionResolveException {
            //stub
        }

        public String getArtifactType() {
            return ".m1";
        }
        
        public Class<M1> getModelType() {
            return M1.class;
        }        
    }
    
    class FileNameArtifactProcessor implements URLArtifactProcessor<M2> {
        public FileNameArtifactProcessor() {
        }

        public M2 read(URL contributionURL, URI uri, URL url) throws ContributionReadException {
            return null;
        }
        
        public void resolve(M2 m2, ModelResolver resolver) throws ContributionResolveException {
            //stub
        }

        public String getArtifactType() {
            return "file.m2";
        }
        
        public Class<M2> getModelType() {
            return M2.class;
        }        
    }

}
