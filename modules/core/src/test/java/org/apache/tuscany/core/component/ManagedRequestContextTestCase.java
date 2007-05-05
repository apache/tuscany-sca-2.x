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
package org.apache.tuscany.core.component;

import org.osoa.sca.RequestContext;

import org.apache.tuscany.core.component.RequestContextImpl;
import org.apache.tuscany.spi.component.WorkContext;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ManagedRequestContextTestCase extends TestCase {

    public void testGetServiceName() {
        WorkContext workContext = EasyMock.createMock(WorkContext.class);
        EasyMock.expect(workContext.getCurrentServiceName()).andReturn("foo");
        EasyMock.replay(workContext);
        RequestContext context = new RequestContextImpl(workContext);
        assertEquals("foo", context.getServiceName());
        EasyMock.verify(workContext);
    }

}
