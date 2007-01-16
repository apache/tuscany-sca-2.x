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

package org.apache.tuscany.runtime.webapp;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

public class LazyHTTPSessionIdTestCase extends TestCase {
    
    public void testGetIdentifier() {
        HttpSession id = createMock(HttpSession.class);

        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getSession(true)).andReturn(id);
        replay(request);
        
        LazyHTTPSessionId lazyHTTPSessionId = new LazyHTTPSessionId(request);
        assertEquals(id, lazyHTTPSessionId.getIdentifier());
    }

}
