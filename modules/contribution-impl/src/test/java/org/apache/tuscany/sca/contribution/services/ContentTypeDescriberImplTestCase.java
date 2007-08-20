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
package org.apache.tuscany.sca.contribution.services;

import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.ContentType;
import org.apache.tuscany.sca.contribution.service.impl.ArtifactTypeDescriberImpl;

public class ContentTypeDescriberImplTestCase extends TestCase {
    private ArtifactTypeDescriberImpl contentTypeDescriber;

    public void testResolveContentType() throws Exception {
        URL artifactURL = getClass().getResource("/test.composite");
        assertEquals(ContentType.COMPOSITE, contentTypeDescriber.getType(artifactURL, null));
    }

    
    public void testResolveUnknownContentType() throws Exception {
        URL artifactURL = getClass().getResource("/test.ext");
        assertNull(contentTypeDescriber.getType(artifactURL, null));
    }
    
    public void testDefaultContentType() throws Exception {
        URL artifactURL = getClass().getResource("/test.ext");
        assertEquals("application/vnd.tuscany.ext", 
                contentTypeDescriber.getType(artifactURL, "application/vnd.tuscany.ext"));        
    }

    @Override
    protected void setUp() throws Exception {
        contentTypeDescriber = new ArtifactTypeDescriberImpl();
    }

}
