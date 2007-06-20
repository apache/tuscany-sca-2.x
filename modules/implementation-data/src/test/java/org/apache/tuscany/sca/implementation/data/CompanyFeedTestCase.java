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
package org.apache.tuscany.sca.implementation.data;

import java.io.IOException;
import java.net.Socket;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * @version $Rev: 543175 $ $Date: 2007-05-31 09:09:12 -0700 (Thu, 31 May 2007) $
 */
public class CompanyFeedTestCase extends TestCase {
    private SCADomain scaDomain;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        scaDomain = SCADomain.newInstance("data-feed.composite");
        //System.in.read();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        scaDomain.close();
    }

    public void testPing() throws IOException {
        new Socket("127.0.0.1", 8085);
    }

}
