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

package org.apache.tuscany.sca.webapp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.easymock.EasyMock;

public class SCADomainHelperTestCase extends TestCase {

    public void testGetDomainURI() {
        ServletContext servletContext = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(servletContext.getInitParameter(SCADomainHelper.DOMAIN_URI_PARAM)).andReturn("http://foo");
        EasyMock.replay(servletContext);

        assertEquals("http://foo", SCADomainHelper.getDomainURI(servletContext));

        EasyMock.verify(servletContext);
    }

    public void testGetDomainURIDefault() {
        ServletContext servletContext = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(servletContext.getInitParameter(SCADomainHelper.DOMAIN_URI_PARAM)).andReturn(null);
        EasyMock.expect(servletContext.getContextPath()).andReturn("/myWebapp");
        EasyMock.replay(servletContext);

        assertEquals("http://myWebapp", SCADomainHelper.getDomainURI(servletContext));

        EasyMock.verify(servletContext);
    }

    public void testgetContrabutionLocation() {
        ServletContext servletContext = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(servletContext.getInitParameter(SCADomainHelper.CONTRABUTION_LOCATION_PARAM)).andReturn("/meta-inf/sca");
        EasyMock.replay(servletContext);

        assertEquals("/meta-inf/sca", SCADomainHelper.getContrabutionLocation(servletContext));

        EasyMock.verify(servletContext);
    }

    public void testGetContrabutionLocationDefault() {
        ServletContext servletContext = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(servletContext.getInitParameter(SCADomainHelper.CONTRABUTION_LOCATION_PARAM)).andReturn(null);
        EasyMock.replay(servletContext);

        assertEquals(".", SCADomainHelper.getContrabutionLocation(servletContext));

        EasyMock.verify(servletContext);
    }

    public void testGetDeployableComposites() {
        ServletContext servletContext = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(servletContext.getInitParameter(SCADomainHelper.DEPLOYABLE_COMPOSITES_PARAM)).andReturn("petra,beate");
        EasyMock.replay(servletContext);

        String[] composites = SCADomainHelper.getDeployableComposites(servletContext);
        assertTrue(composites.length == 2);
        assertEquals("petra", composites[0]);
        assertEquals("beate", composites[1]);

        EasyMock.verify(servletContext);
    }

    public void testGetDeployableCompositesDir() {
        ServletContext servletContext = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(servletContext.getInitParameter(SCADomainHelper.DEPLOYABLE_COMPOSITES_PARAM)).andReturn(null);
        EasyMock.expect(servletContext.getInitParameter(SCADomainHelper.DEPLOYABLE_COMPOSITES_DIR_PARAM)).andReturn("/myDir");
        Set<String> resources = new HashSet<String>();
        resources.add("foo");
        resources.add("/myDir/petra.composite");
        resources.add("/myDir/beate.composite");
        EasyMock.expect(servletContext.getResourcePaths("/myDir")).andReturn(resources);
        EasyMock.replay(servletContext);

        String[] composites = SCADomainHelper.getDeployableComposites(servletContext);
        assertTrue(composites.length == 2);
        assertTrue(Arrays.asList(composites).contains("petra.composite"));
        assertTrue(Arrays.asList(composites).contains("beate.composite"));

        EasyMock.verify(servletContext);
    }

    public void testGetDeployableCompositesDefaultDir() {
        ServletContext servletContext = EasyMock.createMock(ServletContext.class);
        EasyMock.expect(servletContext.getInitParameter(SCADomainHelper.DEPLOYABLE_COMPOSITES_PARAM)).andReturn(null);
        EasyMock.expect(servletContext.getInitParameter(SCADomainHelper.DEPLOYABLE_COMPOSITES_DIR_PARAM)).andReturn(null);
        Set<String> resources = new HashSet<String>();
        resources.add("foo");
        resources.add("/WEB-INF/classes/petra.composite");
        resources.add("/WEB-INF/classes/beate.composite");
        EasyMock.expect(servletContext.getResourcePaths("/WEB-INF/classes")).andReturn(resources);
        EasyMock.replay(servletContext);

        String[] composites = SCADomainHelper.getDeployableComposites(servletContext);
        assertTrue(composites.length == 2);
        assertTrue(Arrays.asList(composites).contains("petra.composite"));
        assertTrue(Arrays.asList(composites).contains("beate.composite"));

        EasyMock.verify(servletContext);
    }
}
