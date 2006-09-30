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
package org.apache.tuscany.core.wire;

import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class SynchronousBridgingInterceptorTestCase extends TestCase {

    public void testInvoke() throws Exception {
        Message msg = new MessageImpl();
        Interceptor next = EasyMock.createMock(Interceptor.class);
        EasyMock.expect(next.invoke(EasyMock.eq(msg))).andReturn(msg);
        EasyMock.replay(next);
        Interceptor interceptor = new SynchronousBridgingInterceptor(next);
        interceptor.invoke(msg);
        verify(next);
    }

}
